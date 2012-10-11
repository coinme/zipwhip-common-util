package com.zipwhip.reliable;

import com.zipwhip.exceptions.DatabaseException;
import com.zipwhip.reliable.retry.RetryStrategy;
import com.zipwhip.util.CollectionUtil;
import com.zipwhip.util.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Erickson
 * Date: 6/25/12
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public final class ReliableDeliveryService {

    private static final Logger logger = LoggerFactory.getLogger(ReliableDeliveryService.class);
    
    //The database which holds all the relevant information about jobs to be worked on.
    private ReliableDeliveryDatabase database;
    //Provides the ReliableDeliveryWorker to be used, based on the work unit type.
    private Map<String, ReliableDeliveryWorker> workerLocator = new HashMap<String, ReliableDeliveryWorker>();
    //The retry strategy to be used if a given work unit fails, assuming that a custom retry strategy has not been provided for the specified work unit type.
    private RetryStrategy defaultRetryStrategy;
    //A holder for custom retry strategies.
    private Map<String, RetryStrategy> customRetryStrategies = new HashMap<String, RetryStrategy>();
    //A set containing all jobs actively being worked on by the ReliableDeliveryService.  Used as one of several failsafes to
    //make sure that a given work unit isn't processed concurrently.
    private Set<String> activeJobs = new HashSet<String>();
    //By default, we'll make a call to clean the attached database 5% of the time.
    private double databaseCleaningRatePercentage = 5d;
    //The number of actively running heartbeats in the application.
    private int heartbeatCount = 0;
    private int maxAttemptCount = 100;

    public ReliableDeliveryService(){

    }
    
    public ReliableDeliveryService(ReliableDeliveryDatabase database, Map<String, ReliableDeliveryWorker> workerLocator, RetryStrategy defaultRetryStrategy, Map<String, RetryStrategy> customRetryStrategies){
        this.database = database;
        this.workerLocator = workerLocator;
        this.defaultRetryStrategy = defaultRetryStrategy;
        this.customRetryStrategies = customRetryStrategies;
    }

    /**
     * @param type The type of work unit to be enqueued.
     * @param parameters The parameters to be associated with the specified work unit.
     * @return The unique key of the newly enqueued work unit.
     * @throws IllegalStateException If the database or default retry strategy are not defined, or if there is no equivalent worker for the supplied work type.
     * @throws com.zipwhip.exceptions.DatabaseException If an error occurs saving the new work unit to the database.
     * @throws IllegalArgumentException If the worker associated with the given work type determines that the supplied parameters are invalid.
     * @throws java.io.IOException If an error occurs while attempting to serialize the parameters object.
     */
    public String enqueueWork(String type, Serializable parameters) throws DatabaseException, IllegalArgumentException, IOException {
        if (this.database == null){
            throw new IllegalStateException("A backing database has not been supplied.");
        } else if (this.defaultRetryStrategy == null){
            throw new IllegalStateException("No default retry strategy has been supplied.");
        }

        //Confirm that we have a worker that supports the supplied work type.
        ReliableDeliveryWorker worker = this.workerLocator.get(type);
        if (worker == null){
            throw new IllegalStateException("Attempting to enqueue an operation of type '" + type + "' for which there is no equivalent worker.");
        }

        //Confirm that the supplied parameters are valid.
        worker.validate(parameters);
        byte[] paramBytes = serializeParameters(parameters);

        synchronized (this){
            //Enqueue the work unit to the database.  The database will return the unique key of the newly enqueued work unit.
            return this.database.enqueue(type, paramBytes);
        }
    }

    /**
     * Tell the com.com.zipwhip.reliable delivery service to run a heartbeat operation, which queries the database for a set unit or work, processes
     * each individual unit of work, and records the results.
     * The service maintains synchronization locks, so it is possible to run multiple heartbeats at the same time, and two
     * concurrently running heartbeats will not process the same unit or work
     */
    public void runHeartbeat() {
        if (this.database == null){
            throw new IllegalStateException("A backing database has not been supplied.");
        } else if (this.defaultRetryStrategy == null){
            throw new IllegalStateException("No default retry strategy has been supplied.");
        }

        List<ReliableDeliveryWork> workUnits = null;

        try {
            //Load the elements to be worked on from the database component.
            synchronized(this){

                heartbeatCount++;

                workUnits = this.database.getIncompleteWork(new HashSet<String>());
                if (CollectionUtil.isNullOrEmpty(workUnits)){
                    //If the database doesn't return anything to work on, don't proceed.  Return here, and let the finally block do it's thing.
                    return;
                }

                //Add each of the work units that we've received to the activeJobs list.  This is an added failsafe, in case
                //a subsequent overlapping heartbeat returns the same job a second time while the first job is already active.
                //In addition, there's a few extra checks here, just in case.
                //NOTE: Explain why this is possible in a handful of scenarios.
                removeExcludedWorkUnits(workUnits);

                //Mark each of the items as being worked on in the database.  This is because we may have multiple heartbeats running
                //at the same time, and we don't want two different heartbeats to pick up the same unit of work.
                Iterator<ReliableDeliveryWork> i = workUnits.iterator();
                while (i.hasNext()){
                    ReliableDeliveryWork workUnit = i.next();
                    workUnit.setWorkingTimestamp(System.currentTimeMillis());
                    try {
                        this.database.update(workUnit);
                    } catch (DatabaseException e){
                        logger.warn("An error occurred attempting to mark a work unit as being actively worked on.", e);
                        //If, for some unknown reason, we are unable to update this particular work unit in the database,
                        //drop it from the list of work units, as we shouldn't process something that we can't mark in the database
                        //as being actively worked on.
                        i.remove();
                    }
                }

                //If we've reached this point, each of the jobs in workUnits can and will be worked on.
                //As such, add each job to the activeJobs list.
                addToActiveJobsList(workUnits);
            }

            //Now, process each individual work unit.  After each work unit has been processed, update it's values, and commit it back to the database.
            for (ReliableDeliveryWork work : workUnits){
                //Execute the work unit.
                ReliableDeliveryResult result = runWorkUnitExecution(work);
                //Update the work unit object to reflect the new result.
                processWorkUnitResult(work, result);

                synchronized(this){
                    try {
                        this.database.update(work);
                    } catch (DatabaseException e){
                        //In truth, if the database can't be bothered to update a value that the database itself has given to
                        //us, there's not a lot we can do at this point.  Log the exception, and move on.
                        logger.warn("An exception occurred while attempting to update a work unit with unique ID '" + work.getUniqueKey() + "'", e);
                    }
                }
            }
        } finally {
            synchronized (this){
                //Now that we've completed the list of jobs that we received, remove each of the items that we processed
                //from the active job list.
                removeFromActiveJobsList(workUnits);

                if (RandomUtils.randomEvent(this.databaseCleaningRatePercentage)){
                    database.runDatabaseCleanOperation();
                }
                heartbeatCount--;

            }
        }
    }

    /**
     * Pulls up the ReliableDeliveryWorker associated with the supplied work unit, and executes the associated operation.
     * @param work
     * @return The result of the execution of the work unit.
     */
    private ReliableDeliveryResult runWorkUnitExecution(ReliableDeliveryWork work){
        try {
            ReliableDeliveryWorker worker = this.workerLocator.get(work.getWorkType());
            if (worker != null){
                Serializable parameters = deserializeParameters(work.getParameters());
                
                try {
                    worker.validate(parameters);
                } catch (IllegalArgumentException e){
                    //An exception is caught here if, and only if, the applicable parameters have been previously validated
                    //serialized, and have failed validation, only after being unserialized.  As such, there's not a lot
                    //we can do at this point.
                    logger.warn("An error occurred while attempting to deserialize input parameters.", e);
                    return ReliableDeliveryResult.FAILSAFE_BROKEN;
                }

                ReliableDeliveryResult result = worker.execute(parameters);

                //If the result that we get back from the worker is not a valid return type (An example of such
                //a return type is ReliableDeliveryResult.NOT_ATTEMPTED), then there's not a lot we can do.
                //Assume something went horribly wrong, and return FAILSAFE_BROKEN.
                if (!result.isValidReturnValue()){
                    result = ReliableDeliveryResult.FAILSAFE_BROKEN;
                }
                return result;
            } else {
                //NOTE: The exception is completely unnecessary.  It's only there to catch the eye of someone debugging,
                //so that they're fully aware that something huge has screwed up.
                logger.warn("Attempting to process a work unit of type '" + work.getWorkType() + "' for which there is no equivalent worker.", new Exception());

                //The only way for this to happen is to enqueue an operation to the database, update to a new version
                //and in that new version the ReliableDeliveryWorker associated with this particular work type
                //has been renamed or removed.  As such, there's not really anything we can do here.
                return ReliableDeliveryResult.FAILSAFE_BROKEN;

            }
        } catch (IOException e){
            //This exception is thrown in a situation where we're failing to deserialize the input parameters.
            //In theory, this should never happen under normal circumstances, but it's something that we should
            //at least take into consideration.
            logger.warn("An error occurred while attempting to deserialize input parameters.", e);
            return ReliableDeliveryResult.FAILSAFE_BROKEN;
        } catch (Exception e){
            //We aren't catching any explicit exceptions here.  Rather, we're only caring about unchecked exceptions,
            //such as NullPointerException, which could foul up further processing.  In such a circumstance, we really
            //shouldn't retry said object, so the only real option is to classify the result as FAILSAFE_BROKEN.
            logger.warn("An unchecked exception was thrown while attempting to process a '" + work.getWorkType() + "' work unit.", e);
            return ReliableDeliveryResult.FAILSAFE_BROKEN;
        }
    }

    /**
     * Alters the supplied work unit's values, based on the supplied com.com.zipwhip.reliable delivery result, in preparation for saving the
     * work unit back to the database.
     * @param work
     * @param result
     */
    private void processWorkUnitResult(ReliableDeliveryWork work, ReliableDeliveryResult result){
        RetryStrategy retryStrategy  = this.getRetryStrategy(work.getWorkType());

        //Now that the work has been completed, update the necessary values in the work unit accordingly, and save the work unit back to
        //the database.
        work.setLastResultCode(result.intValue());
        work.setWorkingTimestamp(-1);

        //If we failed this particular job, increment the failed attempt count here.
        if (!result.isSuccessful()){
            work.setFailedAttemptCount(work.getFailedAttemptCount()+1);
        }

        if (!result.isAllowsReattempt() || work.getFailedAttemptCount() >= maxAttemptCount){
            //In this case, we will not be attempting this particular work unit, either because
            //the work unit said we should make no attempt to continue (Due to a business side error),
            //or the retry strategy is telling us to give up, because we've reached the limit of attempts made.
            work.setDateCompleted(System.currentTimeMillis());
            work.setNextRetryAttempt(-1);
        } else {
            //In this case, we will be re-attempting this item at some later point.  As such, set the timestamp for the next
            //retry attempt, based on the interval provided by the applicable retry strategy.
            work.setNextRetryAttempt(System.currentTimeMillis() + retryStrategy.getNextRetryInterval(work.getFailedAttemptCount()));
        }

    }

    /**
     * Iterates over the supplied work units, removing all work units which should not be worked on.
     * This can be either due to the fact that the work unit has already been completed, or is currently being run.
     * All modifications are made to the list supplied as input.
     * @param workUnits
     */
    private void removeExcludedWorkUnits(List<ReliableDeliveryWork> workUnits){
        Iterator<ReliableDeliveryWork> i = workUnits.iterator();
        while (i.hasNext()){
            ReliableDeliveryWork toCheck = i.next();

            //If we're already working on the given work unit, skip it.
            boolean skipWorkUnit = activeJobs.contains(toCheck.getUniqueKey());

            //If the work unit has already been completed (For any reason) skip it.
            skipWorkUnit |= toCheck.getDateCompleted() > 0;

            if (skipWorkUnit){
                i.remove();
                workUnits.remove(i);
            }
        }
    }

    /**
     * Adds each of the supplied jobs to the active jobs list.
     * @param workUnits
     */
    private void addToActiveJobsList(List<ReliableDeliveryWork> workUnits){
        for (ReliableDeliveryWork work : workUnits){
            this.activeJobs.add(work.getUniqueKey());
        }
    }

    /**
     * Removes each of the supplied jobs from the active jobs list.
     * @param workUnits
     */
    private void removeFromActiveJobsList(List<ReliableDeliveryWork> workUnits){
        for (ReliableDeliveryWork work : workUnits){
            this.activeJobs.remove(work.getUniqueKey());
        }
    }

    private RetryStrategy getRetryStrategy(String workType){
        if (this.customRetryStrategies == null || !this.customRetryStrategies.containsKey(workType)) return this.defaultRetryStrategy;
        return this.customRetryStrategies.get(workType);
    }

    /**
     * A utility method to serialize input parameters.
     * @param params
     * @return
     * @throws java.io.NotSerializableException
     */
    private byte[] serializeParameters(Serializable params) throws NotSerializableException {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            new ObjectOutputStream(bos).writeObject(params);
            return bos.toByteArray();
        } catch (IOException e){
            e.printStackTrace();
            throw new NotSerializableException();
        }
    }

    /**
     * A utility method to deserialize input paramters.
     * @param bytes
     * @return
     * @throws Exception
     */
    private Serializable deserializeParameters(byte[] bytes) throws Exception {
        return (Serializable)new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
    }

    /**
     *
     * @return The number of heartbeats of the application that are currently running.
     */
    public int getRunningHeartbeatCount() {
        return this.heartbeatCount;
    }

    public ReliableDeliveryDatabase getDatabase() {
        return database;
    }

    public void setDatabase(ReliableDeliveryDatabase database) {
        this.database = database;
    }

    public Map<String, ReliableDeliveryWorker> getWorkerLocator() {
        return workerLocator;
    }

    public void setWorkerLocator(Map<String, ReliableDeliveryWorker> workerLocator) {
        this.workerLocator = workerLocator;
    }

    public RetryStrategy getDefaultRetryStrategy() {
        return defaultRetryStrategy;
    }

    public void setDefaultRetryStrategy(RetryStrategy defaultRetryStrategy) {
        this.defaultRetryStrategy = defaultRetryStrategy;
    }

    public double getDatabaseCleaningRatePercentage() {
        return databaseCleaningRatePercentage;
    }

    public void setDatabaseCleaningRatePercentage(double databaseCleaningRatePercentage) {
        this.databaseCleaningRatePercentage = databaseCleaningRatePercentage;
    }
}

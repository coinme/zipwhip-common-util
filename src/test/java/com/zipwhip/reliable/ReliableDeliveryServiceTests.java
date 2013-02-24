package com.zipwhip.reliable;

import com.zipwhip.reliable.database.MemoryReliableDeliveryDatabase;
import com.zipwhip.reliable.testparams.EmptyReliableDeliveryParameter;
import com.zipwhip.reliable.testparams.SleepIntervalParameter;
import com.zipwhip.reliable.retry.ConstantIntervalRetryStrategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: Erickson
* Date: 7/5/12
* Time: 11:56 AM
* To change this template use File | Settings | File Templates.
*/
public class ReliableDeliveryServiceTests {


    //Denotes a work unit type where the enqueued work unit sleeps for a period of time, equal to 'sleepInterval', and then returns successfully.
    private static String WORK_UNIT_SLEEP = "WorkUnitSleep";
    //Denotes a work unit type where the enqueued work unit sleeps for a period of time, equal to 'sleepInterval', and then returns non-blocking failure.
    private static String WORK_UNIT_SLEEP_FAIL = "WorkUnitSleepFail";
    //Denotes a work unit type where the enqueued work unit sleeps for a period of time, equal to 'sleepInterval', and then returns blocking failure.
    private static String WORK_UNIT_SLEEP_FAIL_BLOCKING = "WorkUnitSleepFailBlocking";
    //Denotes a work unit that increments a static value by one, so which we can use to confirm that work units aren't being re-run multiple times.
    private static String WORK_UNIT_INCREMENTER = "WorkUnitIncrementer";

    private ReliableDeliveryService service;
    private MemoryReliableDeliveryDatabase database;
    private int incrementCount = 0;

    @Before
    public void setUp() throws Exception {
        incrementCount = 0;
        database = new MemoryReliableDeliveryDatabase();
        database.setIncompleteWorkQuerySize(3);
        database.setStallRetryInterval(10000);

        service = new ReliableDeliveryService();
        service.setDatabase(database);
        service.setDefaultRetryStrategy(new ConstantIntervalRetryStrategy(2000)); //Make at most ten attempts, waiting 2 seconds inbetween.
        service.setWorkerLocator(new HashMap<String, ReliableDeliveryWorker>());

        //Add the three types of work units that we'll be processing to the work unit locator here.
        service.getWorkerLocator().put(WORK_UNIT_SLEEP, new ReliableDeliveryWorker<SleepIntervalParameter>() {
            @Override
            public void validate(SleepIntervalParameter parameters) throws IllegalArgumentException {
                validateSleepInterval(parameters);
            }

            @Override
            public ReliableDeliveryResult execute(SleepIntervalParameter parameters) {
                long sleepInterval = ((SleepIntervalParameter)parameters).getInterval();
                try {
                    if (sleepInterval > 0) Thread.sleep(sleepInterval);
                } catch (InterruptedException e){
                    return ReliableDeliveryResult.OPERATION_FAILED_REATTEMPT_NOT_BLOCKING;
                }
                return ReliableDeliveryResult.OPERATION_SUCCESSFUL;
            }
        });

        service.getWorkerLocator().put(WORK_UNIT_SLEEP_FAIL, new ReliableDeliveryWorker<SleepIntervalParameter>() {
            @Override
            public void validate(SleepIntervalParameter parameters) throws IllegalArgumentException {
                validateSleepInterval(parameters);
            }

            @Override
            public ReliableDeliveryResult execute(SleepIntervalParameter parameters) {
                try {
                    long sleepInterval = validateSleepInterval(parameters);
                    if (sleepInterval > 0) Thread.sleep(sleepInterval);
                } catch (IllegalArgumentException e){
                    //In theory, this should never happen.
                    return ReliableDeliveryResult.OPERATION_FAILED_DO_NOT_REATTEMPT;
                } catch (InterruptedException e){
                    return ReliableDeliveryResult.OPERATION_FAILED_REATTEMPT_NOT_BLOCKING;
                }
                return ReliableDeliveryResult.OPERATION_FAILED_REATTEMPT_NOT_BLOCKING;
            }

        });

        service.getWorkerLocator().put(WORK_UNIT_SLEEP_FAIL_BLOCKING, new ReliableDeliveryWorker<SleepIntervalParameter>() {
            @Override
            public void validate(SleepIntervalParameter parameters) throws IllegalArgumentException {
                validateSleepInterval(parameters);
            }

            @Override
            public ReliableDeliveryResult execute(SleepIntervalParameter parameters) {
                try {
                    long sleepInterval = validateSleepInterval(parameters);
                    if (sleepInterval > 0) Thread.sleep(sleepInterval);
                } catch (IllegalArgumentException e){
                    //In theory, this should never happen.
                    return ReliableDeliveryResult.OPERATION_FAILED_DO_NOT_REATTEMPT;
                } catch (InterruptedException e){
                    return ReliableDeliveryResult.OPERATION_FAILED_REATTEMPT_NOT_BLOCKING;
                }
                return ReliableDeliveryResult.OPERATION_FAILED_REATTEMPT_BLOCKING;
            }
        });

        service.getWorkerLocator().put(WORK_UNIT_INCREMENTER, new ReliableDeliveryWorker<EmptyReliableDeliveryParameter>() {
            @Override
            public void validate(EmptyReliableDeliveryParameter parameters) throws IllegalArgumentException {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public ReliableDeliveryResult execute(EmptyReliableDeliveryParameter parameters) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e){

                }
                synchronized(this){
                    ReliableDeliveryServiceTests.this.incrementCount++;
                }
                return ReliableDeliveryResult.OPERATION_SUCCESSFUL;
            }
        });
    }

    @Test
    public void testValidEnqueues() throws Exception {
        this.service.enqueueWork(WORK_UNIT_SLEEP, new SleepIntervalParameter(2000));
        this.service.enqueueWork(WORK_UNIT_SLEEP, new SleepIntervalParameter(0));
        this.service.enqueueWork(WORK_UNIT_SLEEP_FAIL, new SleepIntervalParameter(234000));
        this.service.enqueueWork(WORK_UNIT_SLEEP_FAIL_BLOCKING, new SleepIntervalParameter(20100));

        Assert.assertEquals(this.database.getIncompleteWorkUnitCount(), 4);

    }

    @Test
    public void testBasicHeartbeatControls() throws Exception {
        database.setIncompleteWorkQuerySize(3);
        database.setStallRetryInterval(10000);

        List<ReliableDeliveryWork> workUnits = new ArrayList<ReliableDeliveryWork>();
        for (int i=0; i<10; i++){
            String uniqueKey = this.service.enqueueWork(WORK_UNIT_INCREMENTER, new EmptyReliableDeliveryParameter());
            workUnits.add(this.database.getByUniqueKey(uniqueKey));
        }

        //Before running the first heartbeat, confirm that we have the correct number of items enqueued.
        Assert.assertEquals(this.database.getIncompleteWorkUnitCount(), 10);
        Assert.assertEquals(this.database.getTotalWorkUnitCount(), 10);

        //Now, run the first heartbeat.  Doing this should run and complete the first three work units in the list.
        this.service.runHeartbeat();

        Assert.assertEquals(this.database.getIncompleteWorkUnitCount(), 7);
        Assert.assertEquals(this.database.getTotalWorkUnitCount(), 10);
        Assert.assertEquals(this.incrementCount, 3);

        for (int i=0; i<3; i++){
            ReliableDeliveryWork work = workUnits.get(i);
            Assert.assertTrue(Math.abs(System.currentTimeMillis() - work.getDateCompleted()) < 10000);
            Assert.assertTrue(Math.abs(System.currentTimeMillis() - work.getDateCreated()) < 10000);
            Assert.assertEquals(work.getWorkType(), WORK_UNIT_INCREMENTER);
            Assert.assertEquals(work.getFailedAttemptCount(), 0);
            Assert.assertEquals(work.getLastResultCode(), ReliableDeliveryResult.OPERATION_SUCCESSFUL.intValue());
            Assert.assertEquals(work.getWorkingTimestamp(), -1l);
            Assert.assertEquals(work.getNextRetryAttempt(), -1l);
        }

        //Now, let's do another heartbeat.
        this.service.runHeartbeat();

        Assert.assertEquals(this.database.getIncompleteWorkUnitCount(), 4);
        Assert.assertEquals(this.database.getTotalWorkUnitCount(), 10);
        Assert.assertEquals(this.incrementCount, 6);

        for (int i=0; i<6; i++){
            ReliableDeliveryWork work = workUnits.get(i);
            Assert.assertTrue(Math.abs(System.currentTimeMillis() - work.getDateCompleted()) < 10000);
            Assert.assertTrue(Math.abs(System.currentTimeMillis() - work.getDateCreated()) < 10000);
            Assert.assertEquals(work.getWorkType(), WORK_UNIT_INCREMENTER);
            Assert.assertEquals(work.getFailedAttemptCount(), 0);
            Assert.assertEquals(work.getLastResultCode(), ReliableDeliveryResult.OPERATION_SUCCESSFUL.intValue());
            Assert.assertEquals(work.getWorkingTimestamp(), -1l);
            Assert.assertEquals(work.getNextRetryAttempt(), -1l);
        }

        //Now, two more heartbeats, at which point everything in the list should be completed.
        this.service.runHeartbeat();
        this.service.runHeartbeat();

        Assert.assertEquals(this.database.getIncompleteWorkUnitCount(), 0);
        Assert.assertEquals(this.database.getTotalWorkUnitCount(), 10);
        Assert.assertEquals(this.incrementCount, 10);

        for (int i=0; i<10; i++){
            ReliableDeliveryWork work = workUnits.get(i);
            Assert.assertTrue(Math.abs(System.currentTimeMillis() - work.getDateCompleted()) < 15000);
            Assert.assertTrue(Math.abs(System.currentTimeMillis() - work.getDateCreated()) < 15000);
            Assert.assertEquals(work.getWorkType(), WORK_UNIT_INCREMENTER);
            Assert.assertEquals(work.getFailedAttemptCount(), 0);
            Assert.assertEquals(work.getLastResultCode(), ReliableDeliveryResult.OPERATION_SUCCESSFUL.intValue());
            Assert.assertEquals(work.getWorkingTimestamp(), -1l);
            Assert.assertEquals(work.getNextRetryAttempt(), -1l);
        }
    }

    /**
     * This is similar to testBasicHeartbeatControls, except I'm running the three heartbeat calls simultaneously.
     * @throws Exception
     */
    @Test
    public void testSimultaneousHeartbeatControls() throws Exception {
        database.setIncompleteWorkQuerySize(3);
        database.setStallRetryInterval(10000);

        List<ReliableDeliveryWork> workUnits = new ArrayList<ReliableDeliveryWork>();
        for (int i=0; i<10; i++){
            String uniqueKey = this.service.enqueueWork(WORK_UNIT_INCREMENTER, new EmptyReliableDeliveryParameter());
            workUnits.add(this.database.getByUniqueKey(uniqueKey));
        }

        //Before running the first heartbeat, confirm that we have the correct number of items enqueued.
        Assert.assertEquals(this.database.getIncompleteWorkUnitCount(), 10);
        Assert.assertEquals(this.database.getTotalWorkUnitCount(), 10);

        for (int i=0; i<3; i++){
            new Thread(){
                public void run(){
                    service.runHeartbeat();
                }
            }.start();
        }
        //If the three heartbeats correctly run simultaneously, all of them should be completed within three seconds.
        Thread.sleep(4000);

        Assert.assertEquals(this.database.getIncompleteWorkUnitCount(), 1);
        Assert.assertEquals(this.database.getTotalWorkUnitCount(), 10);
        Assert.assertEquals(this.incrementCount, 9);

        for (int i=0; i<9; i++){
            ReliableDeliveryWork work = workUnits.get(i);
            Assert.assertTrue(Math.abs(System.currentTimeMillis() - work.getDateCompleted()) < 10000);
            Assert.assertTrue(Math.abs(System.currentTimeMillis() - work.getDateCreated()) < 10000);
            Assert.assertEquals(work.getWorkType(), WORK_UNIT_INCREMENTER);
            Assert.assertEquals(work.getFailedAttemptCount(), 0);
            Assert.assertEquals(work.getLastResultCode(), ReliableDeliveryResult.OPERATION_SUCCESSFUL.intValue());
            Assert.assertEquals(work.getWorkingTimestamp(), -1l);
            Assert.assertEquals(work.getNextRetryAttempt(), -1l);
        }
    }

    @Test
    public void testEnqueuesWithInvalidWorkUnitTypes(){
        try {
            this.service.enqueueWork("ALSKDJALKSJDALJSKDJAKLS", new EmptyReliableDeliveryParameter());
            Assert.fail();
        } catch (IllegalStateException e){
            //The correct answer.
        } catch (Exception e){
            Assert.fail();
        }

    }

    @Test
    public void testEnqueuesWithInvalidParameters(){
        try {
            this.service.enqueueWork(WORK_UNIT_SLEEP, new EmptyReliableDeliveryParameter());
            Assert.fail();
        } catch (Exception e){
            //The correct answer.
        }

        try {
            this.service.enqueueWork(WORK_UNIT_SLEEP, new SleepIntervalParameter(-23434));
            Assert.fail();
        } catch (IllegalArgumentException e){
            //The correct answer.
        } catch (Exception e){
            Assert.fail();
        }
    }

    /**
     * This test uses a database with an very small stall retry interval, so that in situations where we're running multiple close
     * together heartbeats, we would expect the database to send it work to be processed that is already being worked on.  However, because
     * the reliable delivery database keeps track of what it's actively working on, we would expect it to exlucde all items that the database returns twice.
     */
    @Test
    public void testSuckyStallRetryInterval() throws Exception {
        database.setStallRetryInterval(10);
        database.setIncompleteWorkQuerySize(5);


        List<ReliableDeliveryWork> workUnits = new ArrayList<ReliableDeliveryWork>();
        for (int i=0; i<10; i++){
            String uniqueKey = this.service.enqueueWork(WORK_UNIT_INCREMENTER, new EmptyReliableDeliveryParameter());
            workUnits.add(this.database.getByUniqueKey(uniqueKey));
        }

        //Before running the first heartbeat, confirm that we have the correct number of items enqueued.
        Assert.assertEquals(this.database.getIncompleteWorkUnitCount(), 10);
        Assert.assertEquals(this.database.getTotalWorkUnitCount(), 10);

        //Run the heartbeat asynchronously.
        new Thread(){
            public void run(){
                service.runHeartbeat();
            }
        }.start();
        Thread.sleep(2500);

        //At this point, two jobs should have been completed.
        Assert.assertEquals(this.database.getIncompleteWorkUnitCount(), 8);

        //On the second heartbeat, because we have a sucky stall retry interval, the heartbeat will be given 5 jobs, of which three
        //are currently being worked on (And should be skipped), and two are new, valid jobs.
        new Thread(){
            public void run(){
                service.runHeartbeat();
            }
        }.start();

        //Wait for all current work to complete.
        Thread.sleep(6000);

        Assert.assertEquals(this.database.getIncompleteWorkUnitCount(), 3);
        Assert.assertEquals(this.incrementCount, 7);
    }






    //Utility method for properly extracting the sleep interval from the parameters list.
    private static long validateSleepInterval(SleepIntervalParameter params) throws IllegalArgumentException {

        if (params == null) throw new IllegalArgumentException("No sleep interval provided");
        if (!(params instanceof SleepIntervalParameter)) throw new IllegalArgumentException("Sleep interval is not a long.");
        try {
            long sleepInterval = ((SleepIntervalParameter)params).getInterval();
            if (sleepInterval < 0){
                throw new IllegalArgumentException("Sleep interval cannot be a negative number.");
            }
            return sleepInterval;
        } catch (NumberFormatException e){
            throw new IllegalArgumentException("Sleep interval is not a valid number.");
        }
    }

}



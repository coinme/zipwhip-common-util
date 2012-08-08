package com.zipwhip.reliable;

import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Erickson
 * Date: 6/25/12
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReliableDeliveryWorkImpl implements ReliableDeliveryWork {

    //A uniquely generated key that denotes a particular unit of work.  No two units of work should have the same unique key.
    private String uniqueKey;

    //The type of work that this particular item represents.
    private String workType;

    //A timestamp representing when this task was enqueued.
    private long dateCreated;

    //A timestamp representing when this task was completed, or when it was dropped.  A timestamp of -1 indicates that this work unit has not been completed.
    //If this field contains a valid timestamp, no attempt should be made to re-attempt this task.
    private long dateCompleted;

    //The input parameters associated with the given work unit.
    private byte[] parameters;

    //The result code of the last attempt made for this work unit or NOT_ATTEMPTED(0) if this particular item of work has not yet been attempted.
    private int lastResultCode;

    //The number of times that
    private int failedAttemptCount;

    //A timestamp representing when this task should be re-attempted next.  A timestamp of -1 denotes we have never attempted this particular unit of work, or that it should be worked on at the next available opportunity.
    private long nextRetryAttempt;

    //Denotes whether or not this work unit is being actively worked on, as well as when it was last queried to be actively worked on.  In situations where this item is not being actively worked on,
    //this value will be -1.  We use a timestamp instead of a boolean to account for situations where the work unit is loaded, but does not complete.  We want to avoid situations where the work
    //unit is forever marked as being worked on, and ends up getting ignored in the future.
    private long workingTimestamp;

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public long getDateCompleted() {
        return dateCompleted;
    }

    public void setDateCompleted(long dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public byte[] getParameters() {
        return parameters;
    }

    public void setParameters(byte[] params) {
        this.parameters = params;
    }

    public int getLastResultCode() {
        return lastResultCode;
    }

    public void setLastResultCode(int lastResultCode) {
        this.lastResultCode = lastResultCode;
    }

    public int getFailedAttemptCount() {
        return failedAttemptCount;
    }

    public void setFailedAttemptCount(int failedAttemptCount) {
        this.failedAttemptCount = failedAttemptCount;
    }

    public long getNextRetryAttempt() {
        return nextRetryAttempt;
    }

    public void setNextRetryAttempt(long nextRetryAttempt) {
        this.nextRetryAttempt = nextRetryAttempt;
    }

    public long getWorkingTimestamp() {
        return workingTimestamp;
    }

    public void setWorkingTimestamp(long workingTimestamp) {
        this.workingTimestamp = workingTimestamp;
    }

    /**
     * Generates a new ReliableDeliveryWorkImpl, with a UUID generated unique key, and all values initialized to their defaults.
     * @param workType The work type of the work unit to be generated.
     * @param parameters The serialized equivalent of the parameters to be associated with the supplied work unit.
     * @return The newly generated and initialized work unit.
     */
    public static ReliableDeliveryWork generateReliableDeliveryWork(String workType, byte[] parameters){
        ReliableDeliveryWork work = new ReliableDeliveryWorkImpl();
        initializeToDefaults(work, workType, parameters);
        return work;
    }
    
    /**
     * Initializes a supplied work unit, with a newly-generated UUID unique key, and all values initialized to their defaults.
     * @param work The work unit to be initialized.
     * @param workType The work type of the work unit to be generated.
     * @param parameters The serialized equivalent of the parameters to be associated with the supplied work unit.
     */
    public static void initializeToDefaults(ReliableDeliveryWork work, String workType, byte[] parameters){
        work.setUniqueKey(UUID.randomUUID().toString());
        work.setWorkType(workType);
        work.setParameters(parameters);
        work.setDateCreated(System.currentTimeMillis());
        work.setFailedAttemptCount(0);
        work.setNextRetryAttempt(-1);
        work.setWorkingTimestamp(-1);
        work.setDateCompleted(-1);
        work.setLastResultCode(ReliableDeliveryResult.NOT_ATTEMPTED.intValue());
    }
}

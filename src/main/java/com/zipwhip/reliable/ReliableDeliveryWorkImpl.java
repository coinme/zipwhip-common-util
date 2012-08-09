package com.zipwhip.reliable;

import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Erickson
 * Date: 6/25/12
 * Time: 4:17 PM
 * A basic implementation of the ReliableDeliveryWork class.  It is not necessary to use this
 * particular class when working with ReliableDeliveryService (And in some cases, such as AndroidTabletTexter, it
 * may not be easily possible).  For explanations of each of the values and their uses, look at the
 * ReliableDeliveryWork interface.
 */
public class ReliableDeliveryWorkImpl implements ReliableDeliveryWork {

    private String uniqueKey;

    private String workType;

    private long dateCreated;

    private long dateCompleted;

    private byte[] parameters;

    private int lastResultCode;

    private int failedAttemptCount;

    private long nextRetryAttempt;

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

package com.zipwhip.reliable;

/**
 * Created by IntelliJ IDEA.
 * User: Erickson
 * Date: 8/2/12
 * Time: 5:28 PM
 * An interface defining the minimum requirements for a unit of work in the com.com.zipwhip.reliable delivery system.  Is equivalent to
 * a database row.  There is a default implementation of this class, ReliableDeliveryWorkImpl, but use of this implementing class
 * is not required.
 */
public interface ReliableDeliveryWork {

    //A uniquely generated key that denotes a particular unit of work.  No two units of work should have the same unique key.
    public void setUniqueKey(String uniqueKey);
    public String getUniqueKey();

    //The type of work that this particular item represents.
    public void setWorkType(String workType);
    public String getWorkType();

    //A timestamp representing when this task was enqueued.
    public void setDateCreated(long dateCreated);
    public long getDateCreated();

    //A timestamp representing when this task was completed, or when it was dropped.  A timestamp of -1 indicates that this work unit has not been completed.
    //If this field contains a valid timestamp, no attempt should be made to re-attempt this task.
    public void setDateCompleted(long dateCompleted);
    public long getDateCompleted();

    //The input parameters associated with the given work unit.
    public void setParameters(byte[] params);
    public byte[] getParameters();

    //The result code of the last attempt made for this work unit or NOT_ATTEMPTED(0) if this particular item of work has not yet been attempted.
    public void setLastResultCode(int lastResultCode);
    public int getLastResultCode();

    //The number of times that this particular work unit has previously failed.
    public void setFailedAttemptCount(int failedAttemptCount);
    public int getFailedAttemptCount();

    //A timestamp representing when this task should be re-attempted next.  A timestamp of -1 denotes we have never attempted this particular unit of work, or that it should be worked on at the next available opportunity.
    public void setNextRetryAttempt(long nextRetryAttempt);
    public long getNextRetryAttempt();

    //Denotes whether or not this work unit is being actively worked on, as well as when it was last queried to be actively worked on.  In situations where this item is not being actively worked on,
    //this value will be -1.  We use a timestamp instead of a boolean to account for situations where the work unit is loaded, but does not complete.  We want to avoid situations where the work
    //unit is forever marked as being worked on, and ends up getting ignored in the future.
    public void setWorkingTimestamp(long workingTimestamp);
    public long getWorkingTimestamp();

}

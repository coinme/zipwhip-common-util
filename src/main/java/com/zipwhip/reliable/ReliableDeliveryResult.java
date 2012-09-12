package com.zipwhip.reliable;

/**
 * Created by IntelliJ IDEA.
 * User: Erickson
 * Date: 6/26/12
 * Time: 10:01 AM
 * Defines the result returnable by the execution of a work unit by a ReliableDeliveryWorker.  Primarily controls
 * whether or not the work unit is reattempted at a later point, and whether or not similar work units are blocked from being attempted.
 */
public enum ReliableDeliveryResult {

    //Represents a work unit that has not yet been attempted.  This value is essentially a placeholder, and should not actually be
    //used as a return code from ReliableDeliveryResult.
    NOT_ATTEMPTED(0, false, true, false),
    //Does exactly what is says it does.
    OPERATION_SUCCESSFUL(1, true, false, true),
    //Denotes that the operation failed, but should not be re-attempted.  Programmatically, this value differs very
    //little from OPERATION_SUCCESSFUL (Neither of them will be reattempted), however, the failedAttemptCount will be incremented
    //before we 'retire' this particular work unit.
    OPERATION_FAILED_DO_NOT_REATTEMPT(2, false, false, true),
    //Denotes a situation where the operation failed, and should be retried.  However, the operation should be considered 'blocking',
    //and operations of a similar type should not be reattempted for some period of time, as there is little to no expectation that
    //they would succeed at this time.
    OPERATION_FAILED_REATTEMPT_BLOCKING(3, false, true, true),
    //Denotes a situation where the operation failed, and should be retried.  The operation is not considered 'blocking'.
    //As such, operations of a similar type are allowed to be attempted at this time, even though this particular operation failed.
    OPERATION_FAILED_REATTEMPT_NOT_BLOCKING(4, false, true, true),
    //Denotes that something has gone horribly wrong while processing a work unit.
    //Examples of things that could cause this:
    // -- A ReliableDeliveryWorker passes back an invalid return value.
    // -- An associated ReliableDeliveryWorker cannot be found for the supplied work unit.
    // -- An exception is thrown while attempting to deserialize input parameters.
    // -- An unchecked exception is thrown in a ReliableDeliveryWorker while attempting to process a work unit.
    FAILSAFE_BROKEN(5, false, false, false);

    private int intValue;
    private boolean allowsReattempt;
    private boolean successful;
    private boolean validReturnValue;

    private ReliableDeliveryResult(int intValue, boolean successful, boolean allowsReattempt, boolean validReturnValue){
        this.intValue = intValue;
        this.allowsReattempt = allowsReattempt;
        this.successful = successful;
        this.validReturnValue = validReturnValue;
    }
    
    public int intValue(){
        return this.intValue;
    }
    
    public static ReliableDeliveryResult getFromInt(int intValue){
        for (ReliableDeliveryResult toCheck : ReliableDeliveryResult.values()){
            if (toCheck.intValue() == intValue) return toCheck;
        }
        return null;
    }

    //Denotes that this particular result allows for reattempts.
    public boolean isAllowsReattempt(){
        return this.allowsReattempt;
    }

    //Denotes that this particular result was successful.
    public boolean isSuccessful(){
        return this.successful;
    }

    /**
     * Denotes that this particular value is a valid return value for ReliableDeliveryWorker.  In situations where
     * the ReliableDeliveryWorker returns an invalid value, it is automatically converted to FAILSAFE_BROKEN.
     */
    public boolean isValidReturnValue(){
        return this.validReturnValue;
    }

}

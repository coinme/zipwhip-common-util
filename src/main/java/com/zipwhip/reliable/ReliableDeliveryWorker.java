package com.zipwhip.reliable;

/**
 * Created by IntelliJ IDEA.
 * User: Erickson
 * Date: 6/25/12
 * Time: 4:45 PM
 * An interface which defines a class capable of processing ReliableDeliveryWork.  The mapping of work workers is handled in the ReliableDeliveryService.
 */
public interface ReliableDeliveryWorker {
    /**
     * Validate the supplied parameters.  Validation is run when a work unit is first enqueued, as well as before being ran.
     * @param parameters The parameters to be validated.
     * @throws IllegalArgumentException To be thrown if the supplied parameters are invalid.
     */
    public void validateParameters(ReliableDeliveryWorkParameters parameters) throws IllegalArgumentException;

    /**
     * Handles the processing of the work unit.
     * @param parameters The parameters necessary to process the supplied work.  In situations where said parameters
     *                   are sent into by way of the ReliableDeliveryService, all parameters have been pre-validated
     *                   before being submitted to this method.
     * @return A ReliableDeliveryResult object, which designates how the supplied work unit should be re-attempted.
     *         Allowed values are OPERATION_SUCCESSFUL, OPERATION_FAILED_DO_NOT_REATTEMPT, OPERATION_FAILED_REATTEMPT_BLOCKING,
     *         and OPERATION_FAILED_REATTEMPT_NOT_BLOCKING.  Returning a null, or any other value will cause the supplied
     *         work unit to be classified as having broken a failsafe, and will not be re-attempted.
     *         In addition, the ReliableDeliveryWorker should make all attempts to catch exceptions internally.  In situations
     *         where an unchecked exception is thrown, the supplied work unit is classified as having broken a failsafe, and
     *         will not be reattempted.
     */
    public ReliableDeliveryResult executeWork(ReliableDeliveryWorkParameters parameters);



    
}

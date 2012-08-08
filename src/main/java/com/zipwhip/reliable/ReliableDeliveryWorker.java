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
     * @param parameters
     * @return
     */
    public ReliableDeliveryResult executeWork(ReliableDeliveryWorkParameters parameters);



    
}

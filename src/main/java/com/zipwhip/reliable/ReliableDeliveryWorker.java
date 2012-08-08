package com.zipwhip.reliable;

/**
 * Created by IntelliJ IDEA.
 * User: Erickson
 * Date: 6/25/12
 * Time: 4:45 PM
 * An interface which defines a class capable of processing ReliableDeliveryWork.  The mapping of work workers is handled in the ReliableDeliveryService.
 */
public interface ReliableDeliveryWorker {

    //TODO: Should we also pass in the work unit type?  This would allow a single work unit to handle several different work unit types, but could also be considered unnnecessary.

    /**
     * Validate the supplied parameters.  Validation is run when a work unit is first enqueued, as well as before being ran.
     * @param parameters The parameters to be validated.
     * @throws IllegalArgumentException To be thrown if the supplied parameters are invalid.
     */
    public void validateParameters(ReliableDeliveryWorkParameters parameters) throws IllegalArgumentException;

//    /**
//     * Converts the provided parameters to a byte array.  It can be safely assumed that the supplied parameters
//     * have already been pre-validated.
//     * @param parameters The parameter object to be serialized.
//     * @return
//     * @throws NotSerializableException If an error occurs during the serialization process.
//     */
//    public byte[] serializeParameters(ReliableDeliveryWorkParameters parameters) throws NotSerializableException;
//
//    /**
//     * Converts the provided byte array to a processable parameters object, which can be consumed through the executeWork call.
//     * @param paramBytes The byte array to be deserialized.
//     * @return
//     * @throws Exception Thrown if an error occurs during deserialization.
//     */
//    public ReliableDeliveryWorkParameters deserializeParameters(byte[] paramBytes) throws Exception;

    /**
     * TODO: Describe
     * @param parameters
     * @return
     */
    public ReliableDeliveryResult executeWork(ReliableDeliveryWorkParameters parameters);



    
}

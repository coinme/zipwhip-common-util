package com.zipwhip.binding.fields;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/27/11
 * Time: 12:36 PM
 *
 * The builders use these for making your DTOs
 */
public interface Field<T> {

    /**
     * Each field has a name.
     *
     * @return
     */
    String getName();

    /**
     * Validate this input as acceptable for this field.
     *
     * This is for basic type checking. This is called first before conversion.
     *
     * @param object
     * @return
     * @throws Exception
     */
    boolean validateRawInput(Object object) throws Exception;

    /**
     * Make sure it's within the bounds you want. (number bounds, etc)
     *
     * @param object
     * @return
     * @throws Exception
     */
    boolean validateBeforeSet(T object) throws Exception;

    /**
     * If the data is valid (determined by a prior validation) then it is converted.
     *
     * @param input
     * @return
     * @throws Exception
     */
    T convert(Object input) throws Exception;

}

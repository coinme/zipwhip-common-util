package com.zipwhip.binding.fields;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/27/11
 * Time: 1:00 PM
 *
 * Convenience class for fields.
 */
public abstract class FieldBase<T> implements Field<T>, Comparable<Field> {

    protected String name;

    public FieldBase(String name) {

        if (name == null){
            throw new NullPointerException("The name must be defined for fields");
        }

        this.name = name;
    }

    public String getName() {
        return name;
    }

    /**
     * Validate the input.
     *
     * @param object The object to be validated
     * @return true if the Object is valid, otherwise false
     * @throws Exception
     */
    abstract public boolean validateRawInput(Object object) throws Exception;

    /**
     * Convert the input Object to the expected output type.
     *
     * @param input The Object to be converted to the output type
     * @return T the converted object
     * @throws Exception
     */
    abstract public T convert(Object input) throws Exception;

    /**
     * Validate the object via {@code validateRawInput} and then set it if it is valid.
     *
     * @param object The object to be validated before being set.
     * @return true if the object was validated and set successfully.
     * @throws Exception
     */
    abstract public boolean validateBeforeSet(T object) throws Exception;

    public int compareTo(Field o) {
        return name.compareTo(o.getName());
    }

}

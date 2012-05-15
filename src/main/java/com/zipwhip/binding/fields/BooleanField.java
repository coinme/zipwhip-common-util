package com.zipwhip.binding.fields;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/27/11
 * Time: 1:08 PM
 *
 * Represents data of type "long"
 */
public class BooleanField extends FieldBase<Boolean> {

    public BooleanField(String name) {
        super(name);
    }

    @Override
    public boolean validateRawInput(Object object) throws Exception {
        return true;
    }

    @Override
    public boolean validateBeforeSet(Boolean object) throws Exception {
        return true;
    }

    @Override
    public Boolean convert(Object input) throws Exception {
        if (input == null){
            return null;
        }

        if (input instanceof Boolean){
            return (Boolean)input;
        }

        // i think this'll crash.
        return Boolean.valueOf(String.valueOf(input));
    }

}

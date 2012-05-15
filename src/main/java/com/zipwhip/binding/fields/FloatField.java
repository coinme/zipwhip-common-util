package com.zipwhip.binding.fields;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/27/11
 * Time: 1:08 PM
 *
 * Represents data of type "long"
 */
public class FloatField extends FieldBase<Float> {

    public FloatField(String name) {
        super(name);
    }

    @Override
    public boolean validateRawInput(Object object) throws Exception {
        return true;
    }

    @Override
    public boolean validateBeforeSet(Float object) throws Exception {
        return true;
    }

    @Override
    public Float convert(Object input) throws Exception {
        if (input == null){
            return null;
        }

        if (input instanceof Float){
            return (Float)input;
        }

        // i think this'll crash.
        return Float.valueOf(String.valueOf(input));
    }

}

package com.zipwhip.binding.fields;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/27/11
 * Time: 1:08 PM
 *
 * Represents data of type "long"
 */
public class IntegerField extends FieldBase<Integer> {

    public IntegerField(String name) {
        super(name);
    }

    @Override
    public boolean validateRawInput(Object object) throws Exception {
        return true;
    }

    @Override
    public boolean validateBeforeSet(Integer object) throws Exception {
        return true;
    }

    @Override
    public Integer convert(Object input) throws Exception {
        if (input == null){
            return null;
        }

        if (input instanceof Integer){
            return (Integer)input;
        }

        // i think this'll crash.
        return Integer.valueOf(String.valueOf(input));
    }

}

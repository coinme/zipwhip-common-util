package com.zipwhip.binding.fields;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/27/11
 * Time: 1:08 PM
 *
 * Represents data of type "long"
 */
public class LongField extends FieldBase<Long> {

    public LongField(String name) {
        super(name);
    }

    @Override
    public boolean validateRawInput(Object object) throws Exception {
        return object instanceof Long;
    }

    @Override
    public boolean validateBeforeSet(Long object) throws Exception {
        return true;
    }

    @Override
    public Long convert(Object input) throws Exception {

        if (input == null){
            return null;
        }

        if (input instanceof Long){
            return (Long) input;
        }

        // i think this'll crash.
        return Long.valueOf(String.valueOf(input));
    }

}

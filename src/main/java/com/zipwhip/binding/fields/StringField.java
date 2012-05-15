package com.zipwhip.binding.fields;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/27/11
 * Time: 12:59 PM
 *
 * For strings
 */
public class StringField extends FieldBase<String> {

    public StringField(String name) {
        super(name);
    }

    @Override
    public boolean validateRawInput(Object object) throws Exception {
        return true;
    }

    @Override
    public boolean validateBeforeSet(String object) throws Exception {
        return true;
    }

    @Override
    public String convert(Object input) throws Exception {
        if (input == null){
            return null;
        }

        return String.valueOf(input);
    }

}

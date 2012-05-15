package com.zipwhip.binding;

import com.zipwhip.binding.fields.Field;

import java.util.Collection;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/27/11
 * Time: 1:59 PM
 *
 * A simple definition of record that exposes the kitchen sink. This is a lazy implementation that you can use.
 * It's lazy because generally you're supposed to subclass the RecordBase with your own strongly typed implementation
 * and hide the fact that you use fields.
 */
public class SimpleRecord extends RecordBase {

    public SimpleRecord(RecordBase record, Long id){
        this(record.fields);

        // set the id
        try {
            this.setRecordId(id);
        } catch (Exception e) {
            // not possible
        }
    }

    public SimpleRecord(Collection<Field> fields) {
        super(fields);
    }

    public SimpleRecord(Field... fields) {
        super(fields);
    }

    public SimpleRecord(Map<String, Field> fields) {
        super(fields);
    }

    public <T> T getValue(Field<T> field) {
        return super.get(field);
    }

    public Map<String, Field> getFields() {
        return super.fields;
    }

    public <T> void set(Field<T> field, T value) {
        super.set(field, value);
    }

    public void set(String field, Object value) {
        try {
            super.set(field, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Used for testing. This should generally not be called because it's cheating!
     */
    protected void fireEvent() {
        onChange.notifyObservers(this, getEventObject());
    }
}

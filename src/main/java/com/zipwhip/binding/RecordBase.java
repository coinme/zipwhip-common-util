package com.zipwhip.binding;

import com.zipwhip.events.DataEventObject;
import com.zipwhip.binding.fields.Field;
import com.zipwhip.binding.fields.LongField;
import com.zipwhip.events.Observable;
import com.zipwhip.events.ObservableHelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/26/11
 * Time: 8:18 PM
 * <p/>
 * Records are mutable containers of data (represented as "fields").
 * They throw events when changed (when u commit the changes).
 */
public abstract class RecordBase implements Record {

    /**
     * The recordId is required for all records. We need to be able to uniquely identify a record (and its dupes)
     * in the stores.
     */
    protected static final Field<Long> FIELD_RECORD_ID = new LongField("id");
    protected static final Object NULL_VALUE = new Object();

    // the "change" event
    protected ObservableHelper<DataEventObject<Record>> onChange;

    // For efficiency we use the same instance over and over again for events.
    private DataEventObject<Record> eventObject;

    // By default we're committing all changes synchronously
    private boolean autoCommit = true;

    // The "perspective" is a running tally of original + changes merged together
    protected Map<Field, Object> perspective = new TreeMap<Field, Object>();

    protected Map<Field, Object> values = new TreeMap<Field, Object>();
    protected Map<Field, Object> changes;
    protected Map<String, Field> fields;

    /**
     * This helps us keep track of what changed.
     */
    private enum Changes {
        SAME, NOT_EXISTS, DIFFERENT
    }

    protected RecordBase(Collection<Field> fields) {

        if (fields == null){
            throw new NullPointerException("Fields cannot be null");
        }

        this.fields = new HashMap<String, Field>(fields.size());

        for (Field field : fields) {
            this.fields.put(field.getName(), field);
        }

        validateFieldsExist();
    }

    protected RecordBase(Field... fields) {
        if (fields == null){
            throw new NullPointerException("Fields cannot be null");
        }

        this.fields = new HashMap<String, Field>(fields.length);
        for (Field field : fields) {
            this.fields.put(field.getName(), field);
        }

        validateFieldsExist();
    }

    protected RecordBase(Map<String, Field> fields) {
        this.fields = fields;
        validateFieldsExist();
    }

    /**
     * Auto commit means that it will internally call commit every time you change a record.
     * This has implications in how often the event is fired.
     *
     * @return true if autoCommit is true, false otherwise
     */
    @Override
    public boolean isAutoCommit() {
        return autoCommit;
    }

    /**
     * Enable/disable auto committing changes.
     *
     * @param autoCommit true if the transactions should be automatically committed
     */
    @Override
    public boolean setAutoCommit(boolean autoCommit) throws Exception {
        this.autoCommit = autoCommit;

        // only commit if auto commit is true.
        if (autoCommit && isDirty()){
            return commit();
        }

        return false;
    }

    /**
     * Alias for setAutoCommit. (It does NOT do internal counting to ensure 3 begins are followed by 3 ends.
     */
    @Override
    public void beginEdit() {
        try {
            this.setAutoCommit(false);
        } catch (Exception e) {
            // can't happen, we're not doing a commit.'
        }
    }

    /**
     * Alias for setAutoCommit. (It does NOT do internal counting to ensure 3 begins are followed by 3 ends.
     */
    @Override
    public boolean endEdit() throws Exception {
        return this.setAutoCommit(true);
    }

    @Override
    public boolean commit() throws Exception {
        if (changes != null) {

            // validate that this record has good data.
            if (!validate()){
                return false;
            }

            // do the commit here.
            values.putAll(changes);
            changes.clear();

            // the perspective does not need to change, it's already up to date.
            changes = null;

            // finish the commit, let everyone know.
            if (this.onChange != null) {
                this.onChange.notifyObservers(this, getEventObject());
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean isDirty() {
        return changes != null;
    }

    @Override
    public boolean validate() throws Exception {
        // the fields/data have already been validated when they were inserted.
        // this is more of a "full record wide" validation.
        // check a "set" of the fields

        // subclasses should override this and provide different implementations of it.

        // only throw if you have no idea if the data is valid or not.

        return true;
    }

    @Override
    public void revert() {
        if (changes != null) {
            // replace the values (eliminating the changes)
            perspective.putAll(values);
            // clear/null the changes
            changes.clear();
            changes = null;
        }
    }

    protected void set(String field, Object value) throws Exception {
        Field f = fields.get(field);

        _set(f, value);
    }

    /**
     * If you are absolutely sure that your input is good (type safe) then use this method to not have to do
     * a manual try/catch
     *
     * @param field
     * @param value
     */
    protected <T> void set(Field<T> field, T value) {
        try {
            _set(field, (Object) value);
        } catch (Exception e) {
            // this wont happen, we did pre-type checking.
            // (might happen if value out of bounds?)
            e.printStackTrace();
        }
    }

    /**
     * This one is a little more private, so i prefixed it with an underscore
     *
     * @param field
     * @param crazyValue
     * @param <T>
     * @throws Exception
     */
    protected <T> void _set(Field<T> field, Object crazyValue) throws Exception {
        if (field == null) {
            throw new NullPointerException("The field cannot be null");
        }

        // prep the value
        Object value = prepareValue(field, crazyValue);

        // check to see if it already existed in values.
        // if it returns false it's a new addition, so we won't flag for changes.
        Changes modification = detectChanges(values, field, value);

        switch (modification) {
            case SAME:
                // noop we dont care.
                return;
            case NOT_EXISTS:
//                values.put(field, value);
//                perspective.put(field, value);
//                // let's not throw an event, since this is not considered a "value change"
//                return;
                break;
            case DIFFERENT:
                // shit, we need to start noticing complicated changes.
                break;
        }

        if (autoCommit) {
            values.put(field, value);
            perspective.put(field, value);
            // we dont need to store the perspective here, because we're in auto commit mode.

            // NOTE: only throw the event when you commit
            // we're in autoCommit mode, so throw the change
            if (onChange != null) {
                onChange.notifyObservers(this, getEventObject());
            }
            return;
        }

        if (changes == null) {
            // this needs to be populated no matter what.
            changes = new HashMap<Field, Object>();
        }

        modification = detectChanges(changes, field, value);

        switch (modification) {
            case SAME:
                // noop we dont care.
                break;
            case NOT_EXISTS:
            case DIFFERENT:
                changes.put(field, value);
                // update the perspective so it has the new data
                perspective.put(field, value);
                // NOTE: only throw event when commit
                // fire that we changed a value.
//                if (onChange != null){
//                    onChange.fireEvent(getEventObject());
//                }
        }

    }

    protected <T> T prepareValue(Field<T> field, Object crazyValue) throws Exception {

        // null wrap
        if (crazyValue == null) {
            crazyValue = NULL_VALUE;
        }

        T value;
        // field definitions are optional
        if (field.validateRawInput(crazyValue)) {
            value = field.convert(crazyValue);
            if (!field.validateBeforeSet(value)) {
                throw new IllegalArgumentException("Validation failed");
            }
        } else {
            throw new IllegalArgumentException("Validation failed");
        }

        return value;
    }

    protected Object get(String key) {
        Field field = fields.get(key);

        return get(field);
    }

    protected <T> T get(Field<T> field) {
        Object result;
        if (changes != null) {
            // "contains" would be a double tap to the data structure.
            result = changes.get(field);
            // we are protecting NULL from being put in here.
            // if it's null, then it's not found.
            if (result != null) {
                if (result == NULL_VALUE) {
                    return null;
                } else {
                    return (T) result;
                }
            }
        }

        result = values.get(field);

        if (result == NULL_VALUE) {
            return null;
        } else {
            return (T) result;
        }
    }

    /**
     * Caching the singleton instance
     *
     * @return
     */
    protected DataEventObject<Record> getEventObject() {

        // lazy create single guy
        if (eventObject == null) {
            eventObject = new DataEventObject<Record>(this, this);
        }

        return eventObject;
    }

    protected Changes detectChanges(Map<Field, Object> data, Field key, Object value) {

        if (data == null) {
            return Changes.NOT_EXISTS;
        }

        Object v = data.get(key);

        if (v == null) {
            // since we protect against null values, this means it's not in the store.
            return Changes.NOT_EXISTS;
        }

        // lets see if these are the same?
        if (v.equals(value)) {
            return Changes.SAME;
        } else {
            return Changes.DIFFERENT;
        }
    }

    private void validateFieldsExist() {
        if (this.fields == null || this.fields.isEmpty()){
            throw new RuntimeException("The fields must be defined for this record");
        }
    }

    public Long getRecordId() {
        return this.get(FIELD_RECORD_ID);
    }

    public void setRecordId(Long id) throws Exception {

        if (get(FIELD_RECORD_ID) != null){
            throw new Exception("Not allowed to update the id if it's already set.");
        }

        set(FIELD_RECORD_ID, id);
    }

    /**
     * onChange will only fire if they commit their changes.
     *
     * @return The observer that will fire on change.
     */
    public Observable<DataEventObject<Record>> onChange() {

        if (onChange == null) {
            onChange = new ObservableHelper<DataEventObject<Record>>();
        }
        return onChange;
    }

}

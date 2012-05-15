package com.zipwhip.binding;

import com.zipwhip.binding.fields.Field;
import com.zipwhip.binding.fields.StringField;
import com.zipwhip.events.DataEventObject;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/27/11
 * Time: 1:58 PM
 *
 * Tests for records in general
 */
public class RecordTest {

    public static final Field<String> FIELD_NAME = new StringField("name");

    private DataBindingMockObserver<DataEventObject<Record>> observer = new DataBindingMockObserver<DataEventObject<Record>>();

    private SimpleRecord record = new SimpleRecord(FIELD_NAME);

    @Before
    public void setUp() throws Exception {
        record.onChange().addObserver(observer);
    }

    @Test
    public void testRevert() throws Exception {

        assertTrue(record.isAutoCommit());

        String currentName = "a";

        record.beginEdit();
        record.set(FIELD_NAME, currentName);
        record.endEdit();

        assertFalse(record.isDirty());

        // first strategy: "autocommit"
        record.setAutoCommit(false);
        record.set(FIELD_NAME, "b");
        assertTrue(record.isDirty());
        assertSame(record.get(FIELD_NAME), "b");
        record.revert();

        // second strategy: "begin but no end"
        record.beginEdit(); // will flag autoCommit false
        assertSame(record.get(FIELD_NAME), currentName);
        record.set(FIELD_NAME, "b");
        assertTrue(record.isDirty());
        assertSame(record.get(FIELD_NAME), "b");
        record.revert();
        assertFalse(record.isDirty());
        assertFalse(record.isAutoCommit());

        assertFalse(record.isDirty());
        assertSame(record.get(FIELD_NAME), currentName);

    }

    @Test
    public void testDirty() throws Exception {

        /**
         * Simple dirty check (begin/end region)
         */
        // not already dirty
        assertFalse(record.isDirty());
        // a quick/simple/basic modify (regardless of autocommit)
        modifyRecordAndCommit(record);
        // make sure it committed
        assertFalse(record.isDirty());

        /**
         * AutoCommit is TRUE
         */
        record.setAutoCommit(true);
        modifyRecord(record);
        // not dirty and observer hit
        assertFalse(record.isDirty());

        /**
         * AutoCommit is FALSE
         */
        record.setAutoCommit(false);
        assertFalse(record.isDirty());
        modifyRecord(record);
        assertTrue(record.isDirty());
        record.commit();
        assertFalse(record.isDirty());
    }

    @Test
    public void testOnChangeAutoCommit() throws Exception {

        // basic setup/assumptions
        assertFalse(observer.hit());
        record.setAutoCommit(true);
        assertFalse(observer.hit());

        /**
         * Modify record and verify event
         */
        modifyRecord(record);
        assertObserverHitAndReset(observer, record);
    }

    @Test
    public void testOnChangeNonAutoCommit() throws Exception {

        // basic setup/assumptions
        assertFalse(observer.hit());
        record.setAutoCommit(false);
        assertFalse(observer.hit());

        /**
         * Modify record, commit, and verify event
         */
        modifyRecord(record);
        assertFalse(observer.hit());
        record.commit();
        assertObserverHitAndReset(observer, record);
    }

    public static void assertObserverHitAndReset(DataBindingMockObserver<? extends DataEventObject> observer, Object record) {
        assertTrue(observer.hit(1));
        assertSame(observer.getEventObject().getData(), record);
        observer.reset();
    }

    /**
     * Will update the value with existing value.
     *
     * @param record
     * @throws Exception
     */
    public static void noopModifyRecordAndCommit(SimpleRecord record) throws Exception {
        if (record.isDirty()){
            record.commit();
        }

        record.beginEdit();
        assertFalse(record.isDirty());

        // this is not a change
        record.set(FIELD_NAME, record.get(FIELD_NAME));

        assertFalse(record.isDirty()); // this son of a bitch is failing to be dirty.

        record.endEdit(); // commit
        assertFalse(record.isDirty());
    }

    /**
     * Simple reusable 1 liner 'edit record and commit'
     * Will work regardless of autoCommit state.
     *
     * @param record
     * @throws Exception
     */
    public static void modifyRecordAndCommit(SimpleRecord record) throws Exception {
        if (record.isDirty()){
            record.commit();
        }

        record.beginEdit();
        assertFalse(record.isDirty());

        modifyRecord(record);
        assertTrue(record.isDirty()); // this son of a bitch is failing to be dirty.

        record.endEdit(); // commit
        assertFalse(record.isDirty());
    }

    public static void modifyRecord(SimpleRecord record) {
        record.set(FIELD_NAME, UUID.randomUUID().toString());
    }

    public static SimpleRecord createRecord() {
        return new SimpleRecord(FIELD_NAME);
    }

    public static SimpleRecord createRecord(boolean autoCommit) throws Exception {
        SimpleRecord record = createRecord();

        record.setAutoCommit(autoCommit);

        return record;
    }

    public static Record createRecord(String name) {
        SimpleRecord record = createRecord();

        record.set(FIELD_NAME, name);

        return record;
    }
}

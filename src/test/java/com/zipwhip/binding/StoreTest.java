package com.zipwhip.binding;

import com.zipwhip.concurrent.DefaultObservableFuture;
import com.zipwhip.concurrent.MutableObservableFuture;
import com.zipwhip.concurrent.ObservableFuture;
import com.zipwhip.events.OrderedDataEventObject;
import com.zipwhip.util.HashCodeComparator;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;
import java.util.EventObject;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/26/11
 * Time: 10:41 PM
 * <p/>
 * Test it
 */
public class StoreTest {

    private DataBindingMockObserver<OrderedDataEventObject<SimpleRecord>> observer = new DataBindingMockObserver<OrderedDataEventObject<SimpleRecord>>();
    private DataBindingMockObserver<EventObject> simpleObserver = new DataBindingMockObserver<EventObject>();

    private DefaultStore<SimpleRecord, Object> store = new DefaultStore<SimpleRecord, Object>();
    private SimpleRecord record = RecordTest.createRecord();

    @Before
    public void setUp() throws Exception {

        // listen for changes
        store.onChange().addObserver(observer);
        store.onAdd().addObserver(observer);
        store.onRemove().addObserver(observer);
        // load is different type
        store.onLoad().addObserver(simpleObserver);

        // register it with the store
        store.add(record);

        /// make sure we start off clean
        observer.reset();
    }

    /**
     * Make sure the record is subscribed to correctly.
     *
     * @throws Exception
     */
    @Test
    public void testEventPropagation() throws Exception {

        // prevent spoiled test
        assertFalse(observer.hit());

        // change the record and commit. we expect the store to propagate the event.
        record.fireEvent();

        // assert that it's hit
        assertObserverHitAndReset();

    }

    @Test
    public void testAddGetRemove() throws Exception {

        // store is prepped!
        assertTrue(store.size() == 1);
        this.assertIndex(record, 0);

        // kill it
        store.remove(record.getRecordId());
        assertObserverHitAndReset(observer, store, record);

        // make sure it really got removed
        assertTrue(store.size() == 0);

        // make sure the methods that previously worked are now broken
        assertNotFound(record, 0);

    }


    @Test
    public void testDataSourceSynchronous() throws Exception {
        testDataSource(new MemoryDataProxy<Object>("Michael Smyers"));
    }

    @Test
    public void testDataSourceAsynchronous() throws Exception {
        testDataSource(new DataProxy<Object>() {
            @Override
            public ObservableFuture<Object> load() throws Exception {

                final MutableObservableFuture<Object> future = new DefaultObservableFuture<Object>(this);

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {

                        }

                        future.setSuccess("Michael Smyers");
                    }
                });

                t.start();

                return future;
            }
        });
    }

    private void testDataSource(DataProxy<Object> proxy) throws Exception {
        // give it a default value
        record.set(RecordTest.FIELD_NAME, "asdf");

        assertTrue(store.size() == 1);
        assertFalse(((SimpleRecord) store.getAt(0)).get(RecordTest.FIELD_NAME).equals("Michael Smyers"));

        final Record[] newRecord = {null};

        // the dataReader takes the data from the proxy and reads it.
        // these two objects are pretty much coupled.
        store.setDataSource(new DataReader<Object>() {

            /**
             * This guy is just a proof of concept
             *
             * @param data in this case, the data is just a raw string.
             * @return returns 1 record where the name is the data
             * @throws Exception
             */
            public Set<Record> read(Object data) throws Exception {
                Set<Record> records = new TreeSet<Record>(HashCodeComparator.getInstance());
                newRecord[0] = RecordTest.createRecord(String.valueOf(data));
                records.add(newRecord[0]);
                return records;
            }
        }, proxy);

        // load (this is asynchronous call?)
        ObservableFuture future = store.load();

        future.awaitUninterruptibly();

        // problem: because of "order of operations" we unblock here before the reader has had a chance to work.
        // i think we need the store to wrap the future? it's odd that it's not ready yet.

        assertTrue(store.size() == 1);
        assertIndex(newRecord[0], 0);
        assertTrue(((SimpleRecord)store.getAt(0)).get(RecordTest.FIELD_NAME).equals("Michael Smyers"));

    }

    @Test
    public void testSort() throws Exception {

        SimpleRecord rec = new SimpleRecord(record, 0L);

        assertIndex(record, 0);
        store.add(rec);
        // make sure "add" event was successful
        assertObserverHitAndReset(observer, store, rec);

        // make sure it adds to the end
        assertIndex(record, 0);
        assertIndex(rec, 1);

        // sort by ID (reversing their indexOf's)
        store.sort(new Comparator<SimpleRecord>() {
            public int compare(SimpleRecord record1, SimpleRecord record2) {
                return record1.getRecordId().compareTo(record2.getRecordId());
            }
        });

        // see if the "load" event happened due to a sort
        assertObserverHitAndReset(simpleObserver);

        // make sure they reversed
        assertIndex(rec, 0);
        assertIndex(record, 1);

    }

    private void assertObserverHitAndReset(DataBindingMockObserver<EventObject> simpleObserver) {
        assertTrue(simpleObserver.hit(1));
        simpleObserver.reset();
    }

    @Test
    public void testFullFilter() throws Exception {

        // check initial conditions
        assertTrue(store.size() == 1);
        assertIndex(record, 0);

        assertFalse(simpleObserver.hit());

        // now lets filter some out
        store.setFilter(new NullFilter<SimpleRecord>());

        // ensure that the full store got wiped
        assertTrue(store.size() == 0);
        assertNotFound(record, 0);

        store.clearFilter();

        // repair the dmg
        assertTrue(store.size() == 1);
        assertIndex(record, 0);

    }

    @Test
    public void test() throws Exception {

    }

    private void assertNotFound(SimpleRecord record, int index) {
        assertFalse(store.contains(record.getRecordId()));
        assertEquals(store.indexOf(record.getRecordId()), -1);
        assertNotSame(store.getAt(index), record);
        assertNotSame(store.get(record.getRecordId()), record);
        assertNull(store.get(record.getRecordId()));
    }

    private void assertIndex(Record record, int index) {
        assertTrue(store.contains(record.getRecordId()));
        assertEquals(store.indexOf(record.getRecordId()), index);
        assertSame(store.getAt(store.indexOf(record.getRecordId())), record);
        assertSame(store.getAt(index), record);
        assertSame(store.get(record.getRecordId()), record);
    }

    @Test
    public void testDupes() throws Exception {

        try {
            // add different one with same id
            store.add(new SimpleRecord(record, record.getRecordId()));
            fail("Did not crash like expected");
        } catch (Exception e) {
            // success!
            // it rejected the dupe
        }

        try {
            // add same one twice
            store.add(record);
            fail("Did not crash like expected");
        } catch (Exception e) {
            // success!
        }

    }

    public static void assertObserverHitAndReset(DataBindingMockObserver<OrderedDataEventObject<SimpleRecord>> observer, Store store, SimpleRecord record) {
        // make sure the observer is hit
        assertTrue(observer.hit(1));
        assertSame(observer.getEventObject().getData(), record);

        // well make sure it's in there first.. it might be the REMOVE event
        if (store.contains(record.getRecordId())){
            assertTrue(observer.getEventObject().getIndex() == store.indexOf(record.getRecordId()));
        }

        // reset it so we can do a new test.
        observer.reset();
    }

    private void assertObserverHitAndReset() {
        assertObserverHitAndReset(observer, store, record);
    }
}

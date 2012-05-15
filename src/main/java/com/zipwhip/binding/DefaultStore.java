package com.zipwhip.binding;

import com.zipwhip.events.*;
import com.zipwhip.util.AutoNumberGenerator;
import com.zipwhip.util.Generator;
import com.zipwhip.concurrent.DefaultObservableFuture;
import com.zipwhip.concurrent.ObservableFuture;
import com.zipwhip.util.KeyValuePair;

import java.util.Comparator;
import java.util.EventObject;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/26/11
 * Time: 8:43 PM
 * <p/>
 * A store contains records. I suspect that only 1 implementation will only ever be needed. I wonder if i could get away
 * without using an interface. My worry is that someone will want to use a different backing store than MixedCollection
 * (one that is more efficient, such as a sophisticated Tree structure), so i went with an interface.
 * <p/>
 * This store is something GUI renderers/builders can use. They bind to the store, and it can be updated when
 * the store reports changes.
 */
public class DefaultStore<T> implements Store {

    private static final Generator<Long> ID_GENERATOR = new AutoNumberGenerator();

    /**
     * A record has been saved/committed.
     */
    protected ObservableHelper<OrderedDataEventObject<Record>> onChange = new ObservableHelper<OrderedDataEventObject<Record>>();
    /**
     * A record has been added.
     */
    protected ObservableHelper<OrderedDataEventObject<Record>> onAdd = new ObservableHelper<OrderedDataEventObject<Record>>();
    /**
     * A record has been removed.
     */
    protected ObservableHelper<OrderedDataEventObject<Record>> onRemove = new ObservableHelper<OrderedDataEventObject<Record>>();

    protected ObservableHelper<EventObject> onLoad = new ObservableHelper<EventObject>();

    protected MixedCollection<Long, Record> data = new MixedCollection<Long, Record>();

    // cached for efficiency
    private EventObject eventObject;

    private DataProxy<T> proxy;
    private DataReader<T> reader;

    // this will listen for dataSource updates
    private Observer<ObservableFuture<T>> onLoadCompleteCallback = new Observer<ObservableFuture<T>>() {

        @Override
        public void notify(Object dataEventObjectObservable, ObservableFuture<T> future) {

            // make sure we use this one
            if (future != lastProxyLoadingFuture){
                // this is not the most current one. ignore this request.
                return;
            }

            try {
                handleImmediatelyDone(future, lastLoadingFuture);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    // for async hits to load() data
    private ObservableFuture<Void> lastLoadingFuture;
    private ObservableFuture<T> lastProxyLoadingFuture;

    public DefaultStore() {

    }

    /**
     * For loading data into this store.
     *
     * @param reader the thing that creates records from raw data
     * @param proxy  the thing that finds raw data somewhere
     */
    public void setDataSource(DataReader<T> reader, DataProxy<T> proxy) {
        this.reader = reader;
        this.proxy = proxy;
    }

    /**
     * Load data via the proxy, and pump the records into the store
     *
     * @throws Exception
     */
    public ObservableFuture<Void> load() throws Exception {

        if (lastLoadingFuture != null) {
            // cancel the old ones.
            lastLoadingFuture.cancel();
            lastProxyLoadingFuture.cancel();
        }

        // this might take a long time.
        // unfortunately i can't sync during it.
        // but this means that after a LOAD operation is requested,
        // any adds that someone does will be overridden by the proxy.load() return value.
        // note: unfortunately this is synchronous.
        lastProxyLoadingFuture = proxy.load();
        lastLoadingFuture = new DefaultObservableFuture<Void>(this);

        // listen for completion of this future.
        lastProxyLoadingFuture.addObserver(onLoadCompleteCallback);

        // this whitespace might be all it takes to miss a callback, so we have to listen first, check second.

        if (handleImmediatelyDone(lastProxyLoadingFuture, lastLoadingFuture)) {
            lastProxyLoadingFuture.removeObserver(onLoadCompleteCallback);
        }

        return lastLoadingFuture;
    }

    /**
     * This future is the future of the proxy
     *
     * @param internal
     * @param external
     * @throws Exception
     * @return if it was done or not
     */
    private synchronized boolean handleImmediatelyDone(ObservableFuture<T> internal, ObservableFuture<Void> external) throws Exception {
        // if it's already done, we need to do some work.
        if (internal.isDone()) {
            if (internal.isSuccess()){
                // already done! shit! we missed it!
                parseAndLoad(internal.getResult());
                external.setSuccess(null);
            } else if (internal.isCancelled()) {
                // cascade the cancellation
                external.cancel();
            } else {
                // must be in error?
                external.setFailure(internal.getCause());
            }
            return true;
        }

        return false;
    }

    private Observer<DataEventObject<Record>> onValueChanged = new Observer<DataEventObject<Record>>() {

        @Override
        public void notify(Object observable, DataEventObject<Record> e) {

            if (e != null && e.getData() != null) {
                if (!contains(e.getData().getRecordId())) {
                    throw new RuntimeException("Assertion error. We've received an event for a record we do not know about. We probably didn't unregister a listener through the remove() event.");
                }
            }

            // one of our records changed!
            // the Observable here is the record.
            if (onChange != null) {
                Record record = e.getData();
                // we need to find the index for this.
                int index = data.indexOfValue(record);
                // rethrow but with an index.
                onChange.notifyObservers(this, getEventObject(e.getData(), index));
            }
        }
    };

    private void remove(Record record) {
        if (record == null) {
            return;
        }

        Long id = record.getRecordId();
        if (!contains(id)) {
            return;
        }

        // need to know where it is so we can update the GUI efficiently
        int index = indexOf(id);
        // kill from our data backing.
        // NOTE: this MUST pierce the veil of filtering
        data.remove(id);
        // stop listening for changes.
        record.onChange().removeObserver(this.onValueChanged);
        // throw
        if (this.onRemove != null) {
            this.onRemove.notifyObservers(this, getEventObject(record, index));
        }

    }

    public synchronized void remove(Long id) {
        Record record = get(id);

        remove(record);
    }

    public synchronized void add(Record record) {

        int index = silentAdd(record);

        if (this.onAdd != null) {
            this.onAdd.notifyObservers(this, new OrderedDataEventObject<Record>(this, record, index));
        }
    }

    private int silentAdd(Record record) {
        if (record == null) {
            throw new NullPointerException("Record is null passed into add() to store");
        }

        Long id = record.getRecordId();

        if (id == null) {
            id = ID_GENERATOR.next();
            try {
                record.setRecordId(id);
            } catch (Exception e) {
                // this can't happen if we have exclusive access to the object (2 threads?)
            }
        }

        Record existing = data.get(id);
        if (existing != null) {
            throw new RuntimeException("The store already contains this");
        }


        // add it to our internal memory
        int index = data.add(id, record);

        // listen for change events. (null safe version)
        record.onChange().addObserver(this.onValueChanged);

        return index;
    }

    public Record get(Long id) {
        return data.get(id);
    }

    public int size() {
        return data.size();
    }

    public Observable<OrderedDataEventObject<Record>> onChange() {
        if (this.onChange == null) {
            this.onChange = new ObservableHelper<OrderedDataEventObject<Record>>();
        }

        return this.onChange;
    }

    public Observable<EventObject> onLoad() {
        return onLoad;
    }

    public Observable<OrderedDataEventObject<Record>> onAdd() {
        return this.onAdd;
    }

    public Observable<OrderedDataEventObject<Record>> onRemove() {
        return this.onRemove;
    }

    public Record getAt(int index) {
        return data.getAt(index);
    }

    public int indexOf(Long id) {
        return data.indexOfKey(id);
    }

    public boolean contains(Long recordId) {
        return data.containsKey(recordId);
    }

    public void sort(final Comparator<Record> comparator) {
        // the 2 different data structures require different API's
        // let's wrap another comparator around to adapt between them.
        data.sort(new Comparator<KeyValuePair<Long, Record>>() {
            public int compare(KeyValuePair<Long, Record> o1, KeyValuePair<Long, Record> o2) {
                return comparator.compare(o1.getValue(), o2.getValue());
            }
        });

        if (onLoad != null) {
            onLoad.notifyObservers(this, getEventObject());
        }
    }

    public void setFilter(final Filter<Record> filter) {
        // do the filter (NOTE: we ignore events from the MixedCollection)
        // so we have to throw our own event manually
        data.setFilter(new Filter<KeyValuePair<Long, Record>>() {

            public boolean call(KeyValuePair<Long, Record> item) throws Exception {
                return filter.call(item.getValue());
            }
        });

        // announce that a full redraw is necessary
        if (onLoad != null) {
            onLoad.notifyObservers(this, new EventObject(this));
        }
    }

    public boolean isFiltered() {
        return data.isFiltered();
    }

    public void clearFilter() {
        data.clearFilter();
    }

    private OrderedDataEventObject<Record> getEventObject(Record record, int index) {
        return new OrderedDataEventObject<Record>(this, record, index);
    }

    private OrderedDataEventObject<Record> getEventObject(Record record) {
        return new OrderedDataEventObject<Record>(this, record, indexOf(record.getRecordId()));
    }

    private void parseAndLoad(T data) throws Exception {
        // parse it into records
        Set<Record> records = reader.read(data);

        synchronized (this) {
            // clear the data first.
            this.data.clear();
            // repopulate.
            // also, dont let anyone else do any adds during this time
            synchronized (records) {
                for (Record record : records) {
                    silentAdd(record);
                }
            }
        }

        // announce the change
        if (onLoad != null) {
            onLoad.notifyObservers(this, getEventObject());
        }
    }

    public EventObject getEventObject() {
        if (eventObject == null) {
            synchronized (this) {
                if (eventObject == null) {
                    eventObject = new EventObject(this);
                }
            }
        }

        return eventObject;
    }
}

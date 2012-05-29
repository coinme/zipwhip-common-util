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
public class DefaultStore<R extends Record, D> implements Store<R> {

    private static final Generator<Long> ID_GENERATOR = new AutoNumberGenerator();

    /**
     * A record has been saved/committed.
     */
    protected ObservableHelper<OrderedDataEventObject<R>> onChange = new ObservableHelper<OrderedDataEventObject<R>>();

    /**
     * A record has been added.
     */
    protected ObservableHelper<OrderedDataEventObject<R>> onAdd = new ObservableHelper<OrderedDataEventObject<R>>();

    /**
     * A record has been removed.
     */
    protected ObservableHelper<OrderedDataEventObject<R>> onRemove = new ObservableHelper<OrderedDataEventObject<R>>();

    /**
     * The complete data set has been loaded or reloaded.
     */
    protected ObservableHelper<EventObject> onLoad = new ObservableHelper<EventObject>();

    /**
     * The backing data keyed by ID.
     */
    protected MixedCollection<Long, R> data = new MixedCollection<Long, R>();

    // cached for efficiency
    private EventObject eventObject;

    private DataProxy<D> proxy;
    private DataReader<D> reader;

    // for async hits to load() data
    private ObservableFuture<Void> lastLoadingFuture;
    private ObservableFuture<D> lastProxyLoadingFuture;

    // this will listen for dataSource updates
    private Observer<ObservableFuture<D>> onLoadCompleteCallback = new Observer<ObservableFuture<D>>() {

        @Override
        public void notify(Object dataEventObjectObservable, ObservableFuture<D> future) {

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

    public DefaultStore() {

    }

    /**
     * For loading data into this store.
     *
     * @param reader the thing that creates records from raw data
     * @param proxy  the thing that finds raw data somewhere
     */
    public void setDataSource(DataReader<D> reader, DataProxy<D> proxy) {
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
    private synchronized boolean handleImmediatelyDone(ObservableFuture<D> internal, ObservableFuture<Void> external) throws Exception {
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
                @SuppressWarnings(value="unchecked") // Guaranteed type safe as R is defined as <R extends Record> and e is a Record which is an interface
                R record = (R) e.getData();

                // we need to find the index for this.
                int index = data.indexOfValue(record);

                // rethrow but with an index.
                onChange.notifyObservers(this, getEventObject(record, index));
            }
        }
    };

    private void remove(R record) {
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

        if (this.onRemove != null) {
            this.onRemove.notifyObservers(this, getEventObject(record, index));
        }

    }

    @Override
    public synchronized void remove(Long id) {
        R record = get(id);
        remove(record);
    }

    @Override
    public synchronized void add(R record) {

        int index = silentAdd(record);

        if (this.onAdd != null) {
            this.onAdd.notifyObservers(this, new OrderedDataEventObject<R>(this, record, index));
        }
    }

    private int silentAdd(R record) {
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

        R existing = data.get(id);
        if (existing != null) {
            throw new RuntimeException("The store already contains this");
        }


        // add it to our internal memory
        int index = data.add(id, record);

        // listen for change events. (null safe version)
        record.onChange().addObserver(this.onValueChanged);

        return index;
    }

    @Override
    public R get(Long id) {
        return data.get(id);
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public Observable<OrderedDataEventObject<R>> onChange() {
        if (this.onChange == null) {
            this.onChange = new ObservableHelper<OrderedDataEventObject<R>>();
        }

        return this.onChange;
    }

    @Override
    public Observable<EventObject> onLoad() {
        return onLoad;
    }

    @Override
    public Observable<OrderedDataEventObject<R>> onAdd() {
        return this.onAdd;
    }

    @Override
    public Observable<OrderedDataEventObject<R>> onRemove() {
        return this.onRemove;
    }

    @Override
    public R getAt(int index) {
        return data.getAt(index);
    }

    @Override
    public int indexOf(Long id) {
        return data.indexOfKey(id);
    }

    @Override
    public boolean contains(Long recordId) {
        return data.containsKey(recordId);
    }

    public void sort(final Comparator<R> comparator) {
        // the 2 different data structures require different API's
        // let's wrap another comparator around to adapt between them.
        data.sort(new Comparator<KeyValuePair<Long, R>>() {
            public int compare(KeyValuePair<Long, R> o1, KeyValuePair<Long, R> o2) {
                return comparator.compare(o1.getValue(), o2.getValue());
            }
        });

        if (onLoad != null) {
            onLoad.notifyObservers(this, getEventObject());
        }
    }

    @Override
    public void setFilter(final Filter<R> filter) {
        // do the filter (NOTE: we ignore events from the MixedCollection)
        // so we have to throw our own event manually
        data.setFilter(new Filter<KeyValuePair<Long, R>>() {

            public boolean call(KeyValuePair<Long, R> item) throws Exception {
                return filter.call(item.getValue());
            }
        });

        // announce that a full redraw is necessary
        if (onLoad != null) {
            onLoad.notifyObservers(this, new EventObject(this));
        }
    }

    @Override
    public boolean isFiltered() {
        return data.isFiltered();
    }

    @Override
    public void clearFilter() {

        data.clearFilter();

        // announce that a full redraw is necessary
        if (onLoad != null) {
            onLoad.notifyObservers(this, new EventObject(this));
        }
    }

    private OrderedDataEventObject<R> getEventObject(R record, int index) {
        return new OrderedDataEventObject<R>(this, record, index);
    }

    private OrderedDataEventObject<R> getEventObject(R record) {
        return new OrderedDataEventObject<R>(this, record, indexOf(record.getRecordId()));
    }

    private void parseAndLoad(D data) throws Exception {

        @SuppressWarnings(value="unchecked") // Guaranteed type safe as R is defined as <R extends Record>
        Set<R> records = (Set<R>) reader.read(data); // parse it into records

        synchronized (this) {
            // clear the data first.
            this.data.clear();
            // repopulate.
            // also, dont let anyone else do any adds during this time
            synchronized (records) {
                for (R record : records) {
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

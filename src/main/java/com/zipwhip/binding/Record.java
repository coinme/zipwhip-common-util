package com.zipwhip.binding;

import com.zipwhip.events.DataEventObject;
import com.zipwhip.events.Observable;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/27/11
 * Time: 1:41 PM
 * <p/>
 * A record contains data. When data changes (when you commit) it throws an event "onChange"
 */
public interface Record extends Modifiable {

    /**
     * Every record must have an ID. It can be null if it has no ID yet.
     *
     * @return The ID of this record on null if it has not been set.
     */
    Long getRecordId();

    /**
     * The record Id is immutable. If it's previously defined, you will get an exception.
     * You can only call this method if the recordId is currently NULL.
     *
     * @param id the id of the record, silly..
     * @throws Exception will throw if value is already set
     */
    void setRecordId(Long id) throws Exception;

    /**
     * When you commit (or when you change a value when AutoCommit is true) it throws the
     * "change" event.
     *
     * @return Observable that is called onChange.
     */
    Observable<DataEventObject<Record>> onChange();

}

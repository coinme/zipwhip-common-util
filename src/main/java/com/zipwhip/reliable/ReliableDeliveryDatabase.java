package com.zipwhip.reliable;

import com.zipwhip.exceptions.DatabaseException;

import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Erickson
 * Date: 6/25/12
 * Time: 4:25 PM
 * Defines the interface necessary to construct a database for use with the ReliableDeliveryService.  Aside from the MemoryReliableDeliveryDatabase,
 * which is really only used for the purposes of testing, there is no specific database implementation, due to the fact that it's expected that
 * the com.com.zipwhip.reliable delivery service would be usable across a wide variety of systems.
 */
public interface ReliableDeliveryDatabase {

    /**
     * Enqueues a new work unit, returning the unique_key of the newly generated work unit.
     * @param type
     * @param params
     * @return
     * @throws com.zipwhip.exceptions.DatabaseException
     */
    public String enqueue(String type, byte[] params) throws DatabaseException;

    /**
     * Returns the ReliableDeliveryWork object associated with the supplied key, or null if no such item is associated with said key.
     * The work unit is returned independent of it's state.  It may already be completed.
     * @param uniqueKey
     * @return
     */
    public ReliableDeliveryWork getByUniqueKey(String uniqueKey);

    /**
     * Returns all current instances of ReliableDeliveryWork that are not yet finished, and can run at this time (Aren't still in their backoff interval).
     * This is used by the ReliableDeliveryService to determine what work should be processed at any given time, as well as the order it should be processed in.
     * (Elements in the beginning of the list are processed first).
     * The set workTypeExclusions denotes work types that should be excluded from the query.
     * @param workTypeExclusions
     * @return
     */
    public List<ReliableDeliveryWork> getIncompleteWork(Set<String> workTypeExclusions);

    /**
     * Updates a designated work unit, based on the work unit's unique key.  In situations where an attempt
     * is made to update an entry in the database for which there is no matching unique key, a DatabaseException is thrown.
     * @param work The work unit whose changes are to be saved in the database.
     * @throws com.zipwhip.exceptions.DatabaseException If an error occurs while updating the supplied value.
     */
    public void update(ReliableDeliveryWork work) throws DatabaseException;

    /**
     * A hook to remind the database to clean itself periodically.  It is not a requirement for the database to actually do anything here,
     * it was just requested that I put in some hook for the database to remind itself to be cleaned.  This operation is called randomly,
     * based on a pre-configured percentage in ReliableDeliveryService.  The cleaning operation is ran after a heartbeat has fully run,
     * but before the heartbeat count has been decremented.  This operation is synchronized so in the ReliableDeliveryService so that
     * it will not run at the same time as other database calls.
     */
    public void runDatabaseCleanOperation();
}

package com.zipwhip.reliable.database;

import com.zipwhip.exceptions.DatabaseException;
import com.zipwhip.reliable.ReliableDeliveryDatabase;
import com.zipwhip.reliable.ReliableDeliveryWork;
import com.zipwhip.reliable.ReliableDeliveryWorkImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
* An in memory implementation of the com.com.zipwhip.reliable delivery database.  It goes without saying that this really shouldn't be used in actual systems, but is more for the purposes of testing.
*/
public class MemoryReliableDeliveryDatabase implements ReliableDeliveryDatabase {

    private List<ReliableDeliveryWork> workUnits = new ArrayList<ReliableDeliveryWork>();

    //Controls the number of items that are returned in a query.
    private int incompleteWorkQuerySize = 2;

    //Controls how long we wait before reattempting a work unit that has been 'stalled' (Was started, but never completed, due to
    //a system shutdown, crash, or critical error.
    private long stallRetryInterval = 10 * 60 * 1000; // 10 minutes.

    public MemoryReliableDeliveryDatabase(){

    }

    @Override
    public String enqueue(String type, byte[] params) throws DatabaseException {
        ReliableDeliveryWork toEnqueue = ReliableDeliveryWorkImpl.generateReliableDeliveryWork(type, params);
        workUnits.add(toEnqueue);
        return toEnqueue.getUniqueKey();
    }

    @Override
    public ReliableDeliveryWork getByUniqueKey(String uniqueKey) {
        for (ReliableDeliveryWork work : workUnits){
            if (uniqueKey.equals(work.getUniqueKey())){
                return work;
            }
        }
        return null;
    }

    @Override
    public List<ReliableDeliveryWork> getIncompleteWork(Set<String> workTypeExclusions) {
        List<ReliableDeliveryWork> retVal = new ArrayList<ReliableDeliveryWork>();
        for (ReliableDeliveryWork work : workUnits){

            if (!canBeWorkedOn(work, workTypeExclusions)) continue;

            retVal.add(work);

            //If we've met or exceeeded the limit for the number of work units to return in a single query, bail out here
            //with what we have so far.
            if (retVal.size() >= this.getIncompleteWorkQuerySize()){
                return retVal;
            }
        }
        return retVal;
    }

    @Override
    public void update(ReliableDeliveryWork work) throws DatabaseException {
        for (int i=0; i<workUnits.size(); i++){
            if (workUnits.get(i).getUniqueKey().equals(work.getUniqueKey())){
                workUnits.set(i, work);
                return;
            }
        }
        throw new DatabaseException();
    }

    @Override
    public void runDatabaseCleanOperation() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private boolean canBeWorkedOn(ReliableDeliveryWork work, Set<String> workTypeExclusions){
        //If the work unit matches a work type exclusion, skip it.
        if (workTypeExclusions.contains(work.getWorkType())) return false;

        //If the work unit has been completed, skip it.
        if (work.getDateCompleted() > 0) return false;

        //If the work unit's nextAttempt timestamp has not yet been reached, skip it.
        if (System.currentTimeMillis() <= work.getNextRetryAttempt()) return false;

        //If the work unit is actively being worked on, skip it.  (Unless it's surpassed the stall period)
        if (System.currentTimeMillis() < work.getWorkingTimestamp() + this.getStallRetryInterval()) return false;

        return true;
    }

    /**
     * A utility method, used for debugging purposes.  Returns the full number of work units in this database.
     * @return
     */
    public int getTotalWorkUnitCount(){
        return this.workUnits.size();
    }

    /**
     * A utility method, used primarily for debugging purposes.  Returns the number of work units that can be worked on
     * at this time, assuming that no work unit exclusions have been specified.
     * @return
     */
    public int getIncompleteWorkUnitCount(){
        int retVal = 0;
        for (ReliableDeliveryWork work : this.workUnits){
            retVal += canBeWorkedOn(work, new HashSet<String>()) ? 1 : 0;
        }

        return retVal;
    }



    public int getIncompleteWorkQuerySize() {
        return incompleteWorkQuerySize;
    }

    public void setIncompleteWorkQuerySize(int incompleteWorkQuerySize) {
        this.incompleteWorkQuerySize = incompleteWorkQuerySize;
    }

    public long getStallRetryInterval() {
        return stallRetryInterval;
    }

    public void setStallRetryInterval(long stallRetryInterval) {
        this.stallRetryInterval = stallRetryInterval;
    }
}

package com.zipwhip.reliable.database;

import com.zipwhip.reliable.ReliableDeliveryResult;
import com.zipwhip.reliable.ReliableDeliveryWork;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
* Created by IntelliJ IDEA.
* User: Erickson
* Date: 7/5/12
* Time: 11:53 AM
* To change this template use File | Settings | File Templates.
*/
public class MemoryReliableDeliveryDatabaseTests {

    private static final String TASK_TYPE_A = "TaskTypeA";
    private static final String TASK_TYPE_B = "TaskTypeB";
    private static final String TASK_TYPE_C = "TaskTypeC";

    private MemoryReliableDeliveryDatabase database;
    private String[] workUnitUniqueKeys = new String[12];

    @Before
    public void setUp() throws Exception {
        this.database = new MemoryReliableDeliveryDatabase();

        this.workUnitUniqueKeys[0] = this.database.enqueue(TASK_TYPE_A, new byte[0]);
        Thread.sleep(10);
        this.workUnitUniqueKeys[1] = this.database.enqueue(TASK_TYPE_A, new byte[0]);
        Thread.sleep(10);
        this.workUnitUniqueKeys[2] = this.database.enqueue(TASK_TYPE_A, new byte[0]);
        Thread.sleep(10);
        this.workUnitUniqueKeys[3] = this.database.enqueue(TASK_TYPE_A, new byte[0]);
        Thread.sleep(10);
        this.workUnitUniqueKeys[4] = this.database.enqueue(TASK_TYPE_B, new byte[0]);
        Thread.sleep(10);
        this.workUnitUniqueKeys[5] = this.database.enqueue(TASK_TYPE_B, new byte[0]);
        Thread.sleep(10);
        this.workUnitUniqueKeys[6] = this.database.enqueue(TASK_TYPE_B, new byte[0]);
        Thread.sleep(10);
        this.workUnitUniqueKeys[7] = this.database.enqueue(TASK_TYPE_B, new byte[0]);
        Thread.sleep(10);
        this.workUnitUniqueKeys[8] = this.database.enqueue(TASK_TYPE_C, new byte[0]);
        Thread.sleep(10);
        this.workUnitUniqueKeys[9] = this.database.enqueue(TASK_TYPE_C, new byte[0]);
        Thread.sleep(10);
        this.workUnitUniqueKeys[10] = this.database.enqueue(TASK_TYPE_C, new byte[0]);
        Thread.sleep(10);
        this.workUnitUniqueKeys[11] = this.database.enqueue(TASK_TYPE_C, new byte[0]);

        //Confirm that each of the values that have been inserted have their initial values properly initialized.
        for (String key : workUnitUniqueKeys){
            ReliableDeliveryWork work = this.database.getByUniqueKey(key);
            Assert.assertEquals(work.getWorkingTimestamp(), -1l);
            Assert.assertEquals(work.getUniqueKey(), key);
            Assert.assertEquals(work.getDateCompleted(), -1l);
            Assert.assertTrue(Math.abs(System.currentTimeMillis() - work.getDateCreated()) < 1000);
            Assert.assertEquals(work.getWorkingTimestamp(), -1l);
            Assert.assertEquals(work.getFailedAttemptCount(), 0);
            Assert.assertEquals(work.getLastResultCode(), ReliableDeliveryResult.NOT_ATTEMPTED.intValue());
            Assert.assertEquals(work.getNextRetryAttempt(), -1l);
        }
    }

    @Test
    public void testGetByUniqueKey(){
        for (int i=0; i<workUnitUniqueKeys.length; i++){
            ReliableDeliveryWork work = this.database.getByUniqueKey(workUnitUniqueKeys[i]);
            Assert.assertNotNull(work);

            if (i >= 0 && i <= 3){
                Assert.assertEquals(work.getWorkType(), TASK_TYPE_A);
            } else if (i >= 4 && i <= 7){
                Assert.assertEquals(work.getWorkType(), TASK_TYPE_B);
            } else if (i >= 8 && i <= 11){
                Assert.assertEquals(work.getWorkType(), TASK_TYPE_C);
            } else {
                Assert.fail("Unrecognized task type");
            }
        }
    }

    @Test
    public void testBasicIncompleteWorkQuery(){
        this.database.setIncompleteWorkQuerySize(3);
        Set<String> exclusions = new HashSet<String>();

        //First, make a query without any exclusions.  We should expect the first three work units that we enqueued.
        List<ReliableDeliveryWork> retValA = this.database.getIncompleteWork(exclusions);
        Assert.assertEquals(retValA.size(), 3);
        Assert.assertEquals(retValA.get(0).getUniqueKey(), this.workUnitUniqueKeys[0]);
        Assert.assertEquals(retValA.get(0).getWorkType(), TASK_TYPE_A);
        Assert.assertEquals(retValA.get(1).getUniqueKey(), this.workUnitUniqueKeys[1]);
        Assert.assertEquals(retValA.get(1).getWorkType(), TASK_TYPE_A);
        Assert.assertEquals(retValA.get(2).getUniqueKey(), this.workUnitUniqueKeys[2]);
        Assert.assertEquals(retValA.get(2).getWorkType(), TASK_TYPE_A);

        //Now, we're making the same query again, and we should get the same results back.  This is just to make sure
        //that we enforce the part where the database doesn't change any values that it returns in this query.
        retValA = this.database.getIncompleteWork(exclusions);
        Assert.assertEquals(retValA.size(), 3);
        Assert.assertEquals(retValA.get(0).getUniqueKey(), this.workUnitUniqueKeys[0]);
        Assert.assertEquals(retValA.get(0).getWorkType(), TASK_TYPE_A);
        Assert.assertEquals(retValA.get(1).getUniqueKey(), this.workUnitUniqueKeys[1]);
        Assert.assertEquals(retValA.get(1).getWorkType(), TASK_TYPE_A);
        Assert.assertEquals(retValA.get(2).getUniqueKey(), this.workUnitUniqueKeys[2]);
        Assert.assertEquals(retValA.get(2).getWorkType(), TASK_TYPE_A);


    }

    @Test
    public void testIncompleteWorkQueryWithWorkTypeExclusions(){
        //Now, let's add an exclusion for work unit type A, and see what happens.  I also tweaked the work unit load value here.
        Set<String> exclusions = new HashSet<String>();
        exclusions.add(TASK_TYPE_A);
        this.database.setIncompleteWorkQuerySize(2);
        List<ReliableDeliveryWork> retValA = this.database.getIncompleteWork(exclusions);
        Assert.assertEquals(retValA.size(), 2);
        Assert.assertEquals(retValA.get(0).getUniqueKey(), this.workUnitUniqueKeys[4]);
        Assert.assertEquals(retValA.get(0).getWorkType(), TASK_TYPE_B);
        Assert.assertEquals(retValA.get(1).getUniqueKey(), this.workUnitUniqueKeys[5]);
        Assert.assertEquals(retValA.get(1).getWorkType(), TASK_TYPE_B);

        //Now let's exclude everything, and see if we get anything back.
        exclusions.add(TASK_TYPE_B);
        exclusions.add(TASK_TYPE_C);

        retValA = this.database.getIncompleteWork(exclusions);
        Assert.assertEquals(retValA.size(), 0);
    }

    @Test
    public void testIncompleteWorkQueryWithWorkTypeCompletions() throws Exception {
        this.database.setIncompleteWorkQuerySize(5);

        ReliableDeliveryWork workA = this.database.getByUniqueKey(this.workUnitUniqueKeys[0]);
        ReliableDeliveryWork workB = this.database.getByUniqueKey(this.workUnitUniqueKeys[2]);
        ReliableDeliveryWork workC = this.database.getByUniqueKey(this.workUnitUniqueKeys[4]);
        workA.setDateCompleted(System.currentTimeMillis());
        workB.setDateCompleted(System.currentTimeMillis());
        workC.setDateCompleted(System.currentTimeMillis());

        //Update the given items in the database, marking them as completed.  Subsequent incomplete work queries should
        //skip these items.
        this.database.update(workA);
        this.database.update(workB);
        this.database.update(workC);

        List<ReliableDeliveryWork> retValA = this.database.getIncompleteWork(new HashSet<String>());
        Assert.assertEquals(retValA.size(), 5);
        Assert.assertEquals(retValA.get(0).getUniqueKey(), this.workUnitUniqueKeys[1]);
        Assert.assertEquals(retValA.get(0).getWorkType(), TASK_TYPE_A);
        Assert.assertEquals(retValA.get(1).getUniqueKey(), this.workUnitUniqueKeys[3]);
        Assert.assertEquals(retValA.get(1).getWorkType(), TASK_TYPE_A);
        Assert.assertEquals(retValA.get(2).getUniqueKey(), this.workUnitUniqueKeys[5]);
        Assert.assertEquals(retValA.get(2).getWorkType(), TASK_TYPE_B);
        Assert.assertEquals(retValA.get(3).getUniqueKey(), this.workUnitUniqueKeys[6]);
        Assert.assertEquals(retValA.get(3).getWorkType(), TASK_TYPE_B);
        Assert.assertEquals(retValA.get(4).getUniqueKey(), this.workUnitUniqueKeys[7]);
        Assert.assertEquals(retValA.get(4).getWorkType(), TASK_TYPE_B);
    }

    /**
     * Test that the stall retry interval works.
     * @throws Exception
     */
    @Test
    public void testIncompleteWorkQueryWithStallRetryInterval() throws Exception {
        this.database.setIncompleteWorkQuerySize(7);
        this.database.setStallRetryInterval(10000); //10 seconds

        ReliableDeliveryWork workA = this.database.getByUniqueKey(this.workUnitUniqueKeys[0]);
        ReliableDeliveryWork workB = this.database.getByUniqueKey(this.workUnitUniqueKeys[2]);
        ReliableDeliveryWork workC = this.database.getByUniqueKey(this.workUnitUniqueKeys[4]);
        ReliableDeliveryWork workD = this.database.getByUniqueKey(this.workUnitUniqueKeys[6]);
        ReliableDeliveryWork workE = this.database.getByUniqueKey(this.workUnitUniqueKeys[7]);

        workA.setWorkingTimestamp(System.currentTimeMillis() - 5000); //Started work 5 seconds ago, should be excluded in query.
        workB.setWorkingTimestamp(System.currentTimeMillis() - 7000); //Started work 7 seconds ago, should be excluded from query.
        workC.setWorkingTimestamp(System.currentTimeMillis() - 15000); //Started work 15 seconds ago, should be included in query.
        workD.setWorkingTimestamp(System.currentTimeMillis() - 60000); //Started work one minute ago, should be included in query.
        workE.setWorkingTimestamp(System.currentTimeMillis() - 2000); //Started work two seconds ago, should be excluded in query.

        //Technically, because this is a memory backed database, we don't actually need to do this update here.
        //However, any file based backing database that copies this script will need to, so I'm leaving this here.
        this.database.update(workA);
        this.database.update(workB);
        this.database.update(workC);
        this.database.update(workD);
        this.database.update(workE);

        List<ReliableDeliveryWork> retValA = this.database.getIncompleteWork(new HashSet<String>());
        Assert.assertEquals(retValA.size(), 7);
        Assert.assertEquals(retValA.get(0).getUniqueKey(), this.workUnitUniqueKeys[1]);
        Assert.assertEquals(retValA.get(0).getWorkType(), TASK_TYPE_A);
        Assert.assertEquals(retValA.get(1).getUniqueKey(), this.workUnitUniqueKeys[3]);
        Assert.assertEquals(retValA.get(1).getWorkType(), TASK_TYPE_A);
        Assert.assertEquals(retValA.get(2).getUniqueKey(), this.workUnitUniqueKeys[4]);
        Assert.assertEquals(retValA.get(2).getWorkType(), TASK_TYPE_B);
        Assert.assertEquals(retValA.get(3).getUniqueKey(), this.workUnitUniqueKeys[5]);
        Assert.assertEquals(retValA.get(3).getWorkType(), TASK_TYPE_B);
        Assert.assertEquals(retValA.get(4).getUniqueKey(), this.workUnitUniqueKeys[6]);
        Assert.assertEquals(retValA.get(4).getWorkType(), TASK_TYPE_B);
        Assert.assertEquals(retValA.get(5).getUniqueKey(), this.workUnitUniqueKeys[8]);
        Assert.assertEquals(retValA.get(5).getWorkType(), TASK_TYPE_C);
        Assert.assertEquals(retValA.get(6).getUniqueKey(), this.workUnitUniqueKeys[9]);
        Assert.assertEquals(retValA.get(6).getWorkType(), TASK_TYPE_C);
    }

    @Test
    public void testIncompleteWorkQueryWithNextRetryAttemptExclusion() throws Exception {
        this.database.setIncompleteWorkQuerySize(7);

        ReliableDeliveryWork workA = this.database.getByUniqueKey(this.workUnitUniqueKeys[0]);
        ReliableDeliveryWork workB = this.database.getByUniqueKey(this.workUnitUniqueKeys[2]);
        ReliableDeliveryWork workC = this.database.getByUniqueKey(this.workUnitUniqueKeys[4]);
        ReliableDeliveryWork workD = this.database.getByUniqueKey(this.workUnitUniqueKeys[6]);
        ReliableDeliveryWork workE = this.database.getByUniqueKey(this.workUnitUniqueKeys[7]);

        workA.setNextRetryAttempt(System.currentTimeMillis() + 5000); //Next attempt is no earlier than 5 seconds from now, should be excluded.
        workB.setNextRetryAttempt(System.currentTimeMillis() + 7000); //Next attempt is no earlier than 7 seconds from now, should be excluded.
        workC.setNextRetryAttempt(System.currentTimeMillis() - 15000); //Next attempt is no earlier than 15 seconds ago, should be included.
        workD.setNextRetryAttempt(System.currentTimeMillis() + 60000); //Next attempt is no earlier than 60 seconds from now, should be excluded.
        workE.setNextRetryAttempt(System.currentTimeMillis() - 2000); //Next attempt is no earlier than 2 seconds ago, should be included.

        //Technically, because this is a memory backed database, we don't actually need to do this update here.
        //However, any file based backing database that copies this script will need to, so I'm leaving this here.
        this.database.update(workA);
        this.database.update(workB);
        this.database.update(workC);
        this.database.update(workD);
        this.database.update(workE);

        List<ReliableDeliveryWork> retValA = this.database.getIncompleteWork(new HashSet<String>());
        Assert.assertEquals(retValA.size(), 7);
        Assert.assertEquals(retValA.get(0).getUniqueKey(), this.workUnitUniqueKeys[1]);
        Assert.assertEquals(retValA.get(0).getWorkType(), TASK_TYPE_A);
        Assert.assertEquals(retValA.get(1).getUniqueKey(), this.workUnitUniqueKeys[3]);
        Assert.assertEquals(retValA.get(1).getWorkType(), TASK_TYPE_A);
        Assert.assertEquals(retValA.get(2).getUniqueKey(), this.workUnitUniqueKeys[4]);
        Assert.assertEquals(retValA.get(2).getWorkType(), TASK_TYPE_B);
        Assert.assertEquals(retValA.get(3).getUniqueKey(), this.workUnitUniqueKeys[5]);
        Assert.assertEquals(retValA.get(3).getWorkType(), TASK_TYPE_B);
        Assert.assertEquals(retValA.get(4).getUniqueKey(), this.workUnitUniqueKeys[7]);
        Assert.assertEquals(retValA.get(4).getWorkType(), TASK_TYPE_B);
        Assert.assertEquals(retValA.get(5).getUniqueKey(), this.workUnitUniqueKeys[8]);
        Assert.assertEquals(retValA.get(5).getWorkType(), TASK_TYPE_C);
        Assert.assertEquals(retValA.get(6).getUniqueKey(), this.workUnitUniqueKeys[9]);
        Assert.assertEquals(retValA.get(6).getWorkType(), TASK_TYPE_C);
    }

    @Test
    public void testUpdate(){
        //I'm well aware that, because this is a memory backed database, the update calls don't really mean that much.
        ReliableDeliveryWork workA = this.database.getByUniqueKey(this.workUnitUniqueKeys[0]);
    }

    @Test
    public void testUpdatesWithInvalidUniqueKeys(){
        //TODO:
    }
}
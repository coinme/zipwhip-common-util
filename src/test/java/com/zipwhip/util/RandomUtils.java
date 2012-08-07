package com.zipwhip.util;

/**
 * Created by IntelliJ IDEA.
 * User: Erickson
 * Date: 3/1/12
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class RandomUtils {

    /**
     * Returns a randomly generate boolean, that is true (percentage) percent of the time.
     * @param percentage
     * @return
     */
    public static boolean randomEvent(int percentage){
        if (percentage <= 0) return false;
        if (percentage >= 100) return true;
        
        long seed = Math.abs((long)Math.floor(Math.random() * System.currentTimeMillis()));
        return seed % 100 < percentage;
    }

    /**
     * Returns a randomly generate boolean, that is true (percentage) percent of the time.
     * @param percentage A value between 0 and 100.  Values greater than or equal to 100 will always return true, while values less than or equal to 0 will always return false.
     * @return
     */
    public static boolean randomEvent(double percentage){
        if (percentage <= 0d) return false;
        if (percentage >= 100d) return true;

        double seed = Math.random() * 100d;
        return seed < percentage;
    }
}

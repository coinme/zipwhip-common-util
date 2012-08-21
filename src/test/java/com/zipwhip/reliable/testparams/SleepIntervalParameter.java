package com.zipwhip.reliable.testparams;


import java.io.Serializable;

/**
* Created by IntelliJ IDEA.
* User: Erickson
* Date: 8/7/12
* Time: 12:41 PM
* To change this template use File | Settings | File Templates.
*/
public class SleepIntervalParameter implements Serializable {
    private long interval;
    public SleepIntervalParameter(long interval){
        this.interval = interval;
    }

    public long getInterval(){
        return this.interval;
    }
}
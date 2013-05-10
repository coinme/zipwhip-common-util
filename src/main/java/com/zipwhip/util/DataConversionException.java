package com.zipwhip.util;

/**
 * Created by IntelliJ IDEA.
 * User: Erickson
 * Date: 11/18/11
 * Time: 12:10 PM
 *
 */
public class DataConversionException extends Exception {

    public DataConversionException(String message){
        super(message);
    }

    public DataConversionException(String message, Exception source){
        super(message, source);
    }

    public DataConversionException(Exception source){
        super(source);
    }

}

package com.zipwhip.exception;

/**
 * Created by IntelliJ IDEA.
 * User: Erickson
 * Date: 8/7/12
 * Time: 1:21 PM
 * Represents a generic database exception, used when we want to throw an exception for something database related, but want to obscure the specific type of database.  For example, we don't want to abstract out to a java.sql.SqlException in
 * situations where the database that we're calling may or may not be a sql database.  I'm well aware that in many ways this functionality overlaps significantly with DtoException.  However, DtoException is primarily specific to zipwhip-lib.
 * As such, there is no equivalent class for code that does not rely on zipwhip-lib.
 */
public class DatabaseException extends Exception {
    
    public DatabaseException(){

    }
    
    public DatabaseException(String message){
        super(message);
    }
    
    public DatabaseException(String message, Exception source){
        super(message, source);
    }
}

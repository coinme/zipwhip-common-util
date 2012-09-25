package com.zipwhip.util.phone;

/**
 * User: Ali Serghini
 * Date: 9/24/12
 * Time: 11:13 AM
 */
public class FallbackParserException extends Exception {
    public FallbackParserException() {
    }

    public FallbackParserException(String message) {
        super(message);
    }

    public FallbackParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public FallbackParserException(Throwable cause) {
        super(cause);
    }
}

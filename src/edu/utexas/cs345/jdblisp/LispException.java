package edu.utexas.cs345.jdblisp;

/**
 * LispException
 */
public class LispException extends Exception {

    public LispException(String message) {
        this(message, null);
    }

    public LispException(String message, Throwable cause) {
        super(message, cause);
    }
}

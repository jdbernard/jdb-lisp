package edu.utexas.cs345.jdb-lisp;

/**
 * InvalidArgumentQuantityException
 * Indicates a call to a form with an incorrect number of arguments.
 */
public class InvalidArgumentQuantityException extends LispException {

    public InvalidArgumentQuantityException(int expected, int actual) {
        super ("Invalid number of arguments: " + actual
            + " (expected " + expected + ").");
    }
}

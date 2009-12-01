package edu.utexas.cs345.jdblisp;

/**
 * InvalidArgumentQuantityException
 * @author Jonathan Bernard (jdbernard@gmail.com)
 * Indicates a call to a form with an incorrect number of arguments.
 */
public class InvalidArgumentQuantityException extends LispException {

    public InvalidArgumentQuantityException(String invokeName, int expected, int actual) {
        super ("Invalid number of arguments to " + invokeName + ": " + actual
            + " (expected " + expected + ").");
    }

    public InvalidArgumentQuantityException(String invokeName, int expected) {
        super ("Invalid number of arguments to " + invokeName + ": expected " + expected);
    }

    public InvalidArgumentQuantityException(String invokeName, String message) {
        super ("Invalid number of arguments to " + invokeName + ": " + message);
    }
}

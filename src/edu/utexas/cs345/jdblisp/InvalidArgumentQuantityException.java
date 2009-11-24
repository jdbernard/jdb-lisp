package edu.utexas.cs345.jdblisp;

/**
 * InvalidArgumentQuantityException
 * @author Jonathan Bernard (jdbernard@gmail.com)
 * Indicates a call to a form with an incorrect number of arguments.
 */
public class InvalidArgumentQuantityException extends LispException {

    public InvalidArgumentQuantityException(int expected, int actual) {
        super ("Invalid number of arguments: " + actual
            + " (expected " + expected + ").");
    }

    public InvalidArgumentQuantityException(int actual) {
        super ("Invalid number of arguments: " + actual);
    }

    public InvalidArgumentQuantityException(String message) {
        super ("Invalid number of arguments: " + message);
    }
}

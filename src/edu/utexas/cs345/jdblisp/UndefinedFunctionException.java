package edu.utexas.cs345.jdblisp;

/**
 * UndefinedFunctionException
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class UndefinedFunctionException extends LispException {

    Symbol symbol;

    public UndefinedFunctionException(Symbol symbol) {
        super("Undefined function: " + symbol.toString());
        this.symbol = symbol;
    }
}

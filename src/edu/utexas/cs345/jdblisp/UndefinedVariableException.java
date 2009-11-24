package edu.utexas.cs345.jdblisp;

/**
 * UndefinedVariableException
 * @author Jonathan Bernard
 */
public class UndefinedVariableException extends LispException {

    public final Symbol symbol;

    public UndefinedVariableException(Symbol symbol) {
        super("Undefined variable: " + symbol.toString());
        this.symbol = symbol;
    }
}

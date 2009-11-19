package edu.utexas.cs345.jdblisp;

/**
 * VariableEntry
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class VariableEntry {

    protected final SExp expression;

    public SExp eval(SymbolTable symbolTable) {
        return expression.eval(symbolTable);
    }
}

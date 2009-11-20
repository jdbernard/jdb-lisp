package edu.utexas.cs345.jdblisp;

/**
 * VariableEntry
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class VariableEntry {

    protected final SExp expression;

    public VariableEntry(SExp expression) { this.expression = expression; }
    
    public SExp eval(SymbolTable symbolTable) throws LispException {
        return expression.eval(symbolTable);
    }
}

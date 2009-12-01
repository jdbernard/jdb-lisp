package edu.utexas.cs345.jdblisp;

import java.util.Map;

/**
 * ClosureSymbolTable
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class ClosureSymbolTable extends SymbolTable {

    private SymbolTable lexicalScope;

    public ClosureSymbolTable(SymbolTable lexicalScope,
    SymbolTable closedScope) {
        super(closedScope);
        this.lexicalScope = lexicalScope;
    }

    /**
     * 
     */
    @Override
    public FormEntry lookupFunction(Symbol s) throws LispException {

        // lookup function in the closed binding first
        FormEntry fe = super.lookupFunction(s);

        // lookup in the lexical scope if it is not found in the closure
        if (fe == null && lexicalScope != null)
            fe = lexicalScope.lookupFunction(s);

        // return the result (may be null if not found anywhere)
        return fe;
    }

    /**
     * Lookup a variable bound to the given
     * {@link edu.utexas.cs345.jdblisp.Symbol}. The lookup is performed first
     * in the closed scope and then the lexical scope if the variable is not
     * found in the closed scope.
     * @param s The symbol to lookup.
     * @return The variable bound to the symbol, or <b>null</b> if the symbol
     * is unbound.
     */
    @Override
    public VariableEntry lookupVariable(Symbol s) throws LispException {

        // lookup variable in the closed binding first
        VariableEntry ve = super.lookupVariable(s);

        // lookup in the lexical scope if it is not found in the closure
        if (ve == null && lexicalScope != null)
            ve = lexicalScope.lookupVariable(s);

        // return the result (may be null if not found anywhere)
        return ve;
    }
}

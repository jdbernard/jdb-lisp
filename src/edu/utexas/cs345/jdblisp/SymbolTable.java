package edu.utexas.cs345.jdblisp;

/**
 * SymbolTable
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class SymbolTable {
    public final TableEntry top;
    public final SymbolTable rest;

    public SymbolTable(TableEntry te, SymbolTable st) {
        this.top = te;
        this.rest = st;
    }

    public SymbolTable(TableEntry... te) {
        int idx = te.length;
        SymbolTable last = null;
        while (idx > 1) last = new SymbolTable(te[--idx], last);

        assert (--idx == 0) : "Error in SymbolTable construction. "
            + "Invalid ending index";

        this.top = te[0];
        this.rest = last;
    }

    public TableEntry findFunction(Symbol symbol) throws LispException {
        if (top.name.equals(symbol) && top.parameters != null)
            return top;

        if (rest == null)
            throw new LispException("Undefined function: "
                + symbol.name);

        return rest.findFunction(symbol);
    }

    public TableEntry findVariable(Symbol symbol) throws LispException {
        // if this entry is the one we're looking for, return it
        if (top.name.equals(symbol) && top.parameters == null)
            return top;

        // if not, and there are no more entries
        if (rest == null)
            throw new LispException("Undefined variable: "
                + symbol.name);

        // otherwise, search the remainder of the table
        return rest.findVariable(symbol);
    }
}

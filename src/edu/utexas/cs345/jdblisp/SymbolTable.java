package edu.utexas.cs345.jdblisp;

/**
 * SymbolTable
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class SymbolTable {

    private Map<Symbol, FunctionEntry> functions = new HashMap<Symbol, FunctionEntry>();
    private Map<Symbol, VariableEntry> variables = new HashMap<Symbol, VariableEntry>();
    private SymbolTable enclosingTable;
    private boolean locked = false;

    public SymbolTable() { this(null, false); }

    public SymbolTable(SymbolTable table) { this(table, false); }

    public SymbolTable(SymbolTable table, boolean locked) {
        this.enclosingTable = table;
        this.locked = locked;
    }

    public Symbol bind(Symbol s, FunctionEntry f) {
        if (functions.get(s) != null) {
            // TODO: warning: function redefinition
            // Also, check access permissions
        }
        functions.put(s, f);
        return s;
    }

    public Symbol bind(Symbol s, VariableEntry v) {
        if (variables.get(s) != null) {
            // TODO: warning: variable redefinition
            // Also, check access permissions
        }

        variables.put(s, v);
        return s;
    }

    public FunctionEntry lookupFunction(Symbol s) throws LispException {
        FunctionEntry fe = functions.get(s);

        // found function definition
        if (fe != null) return fe;

        // did not find function, and there is no outer scope
        if (enclosingTable == null)
            throw new LispException("Undefined function " + s.toString());

        // search outer scope
        return enclosingTable.lookupFunction(s);
    }

    public VariableEntry lookupVariable(Symbol s) throws LispException {
        VariableEntry ve = variables.get(s);

        // found variable entry
        if (ve != null) return ve;

        // did not find variable entry and there is no outer scope
        if (enclosingTable == null)
            throw new LispException("Undefined variable " + s.toString());

        // search outer scope
        return enclosingTable.lookupVariable(s);
    }
}

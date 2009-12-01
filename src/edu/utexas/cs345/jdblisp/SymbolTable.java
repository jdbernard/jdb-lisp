package edu.utexas.cs345.jdblisp;

import java.util.HashMap;
import java.util.Map;

/**
 * SymbolTable
 * @author Jonathan Bernard (jdbernard@gmail.com)
 * A SymbolTable holds the bindings of function definitions and variables for
 * a lexical scope. Functions and variables are maintained seperately, so you
 * can have a variable and function with the same symbol.
 */
public class SymbolTable {

    private Map<Symbol, FormEntry> functions = new HashMap<Symbol, FormEntry>();
    private Map<Symbol, VariableEntry> variables =
        new HashMap<Symbol, VariableEntry>();

    private SymbolTable enclosingTable;
    private boolean locked = false;

    /**
     * Create a new, unlocked, and empty symbol table.
     */
    public SymbolTable() { this(null, false); }

    /**
     * Create a new unlocked SymbolTable enclosed by the given table.
     * @param table The outer, enclosing table.
     */
    public SymbolTable(SymbolTable table) { this(table, false); }

    /**
     * Create a new SymbolTable enclosed by the given table.
     * @param table The outer, enclosing table.
     * @param locked If this table is locked.
     */
    public SymbolTable(SymbolTable table, boolean locked) {
        this.enclosingTable = table;
        this.locked = locked;
    }

    /**
     * Bind a value to a (potentially new) function definition named by the
     * {@link edu.utexas.cs345.jdblisp.Symbol} s.
     * @param s The {@link edu.utexas.cs345.jdblisp.Symbol} to bind to.
     * @param f The {@link edu.utexas.cs345.jdblisp.FormEntry} representing
     * the function definition.
     */
    public Symbol bind(Symbol s, FormEntry f) {
        if (functions.get(s) != null) {
            // TODO: warning: function redefinition
            // Also, check access permissions
        }
        functions.put(s, f);
        return s;
    }

    /**
     * Bind a value to a (potentially new) variable named by the
     * {@link edu.utexas.cs345.jdblisp.Symbol} s.
     * @param s The {@link edu.utexas.cs345.jdblisp.Symbol} to bind.
     * @param c The {@link edu.utexas.cs345.jdblisp.VariableEntry} representing
     * the value to bind.
     */
    public Symbol bind(Symbol s, VariableEntry v) {
        if (variables.get(s) != null) {
            // TODO: warning: variable redefinition
            // Also, check access permissions
        }

        variables.put(s, v);
        return s;
    }

    /**
     * Bind a new value to an existing variable named by the
     * {@link edu.utexas.cs345.jdblisp.Symbol} s.
     * @param s The {@link edu.utexas.cs345.jdblisp.Symbol} to bind.
     * @param v The {@link edu.utexas.cs345.jdblisp.VariableEntry} representing
     * the new value to bind.
     * @throws edu.utexas.cs345.jdblisp.LispException if the variable is not
     * defined or if the variable was defined as a constant.
     */
    public Symbol rebind(Symbol s, VariableEntry v) throws LispException {

        // look up the variable in the current table
        VariableEntry curVal = variables.get(s);

        // not present
        if (curVal == null) {
            
            // no parent table, variable is undefine
            if (enclosingTable == null) 
                throw new LispException("No such variable defined: " + s.name);

            // check parent table
            return enclosingTable.rebind(s, v);

        }
        
        // it is present
        else {

            // if this variable is constant, err
            if (curVal.isConstant)
                throw new LispException("Cannot set variable " + s.name
                    + ": the variable is constant.");

            // if not, set the new value
            variables.put(s, v);
        }

        return s;
    }

    /**
     * Lookup a function definition in this symbol table.
     * @param s The symbol representing the function in question.
     * @return The function definiton of the function bound to the Symbol s
     * or <b>null</b> if there is no function bound to the symbol.
     */
    public FormEntry lookupFunction(Symbol s) throws LispException {
        FormEntry fe = functions.get(s);

        // found function definition
        if (fe != null) return fe;

        // did not find function, and there is no outer scope
        if (enclosingTable == null) return null;

        // search outer scope
        return enclosingTable.lookupFunction(s);
    }

    /**
     * Lookup a variable definition in this symbol table.
     * @param s The symbol representing the variable.
     * @return The variable entry for the given symbol or <b>null</b> if there
     * is no variable bound to the symbol.
     */
    public VariableEntry lookupVariable(Symbol s) throws LispException {
        VariableEntry ve = variables.get(s);

        // found variable entry
        if (ve != null) return ve;

        // did not find variable entry and there is no outer scope
        if (enclosingTable == null) return null;

        // search outer scope
        return enclosingTable.lookupVariable(s);
    }

    /**
     * DEBUG ONLY: traverses the entire scope history.
     */
    public String toString() {
        return toString("");
    }

    private String toString(String enclosedTable) {
        StringBuilder sb = new StringBuilder();
        sb.append("{").append(enclosedTable).append("} V: ");

        for (VariableEntry ve : variables.values())
            sb.append(ve.symbol.toString()).append("=")
                .append(ve.value.toString()).append(" ");

        sb.append("F: ");

        for (FormEntry fe : functions.values())
            sb.append(fe.symbol.toString()).append("=")
                .append(fe.toString()).append(" ");

        sb.append("}");

        if (enclosingTable == null) return sb.toString();
        
        return enclosingTable.toString(sb.toString());
    }
}

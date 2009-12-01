package edu.utexas.cs345.jdblisp;

/**
 * VariableEntry
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class VariableEntry implements SymbolTableEntry {

    public final Symbol symbol;
    public final SExp value;
    public final boolean isConstant;
    public final HelpTopic helpinfo;

    public VariableEntry(Symbol symbol, SExp value) {
        this(symbol, value, false);
    }

    public VariableEntry(Symbol symbol, SExp value, boolean constant) {
        this(symbol, value, constant, null); 
    }

    public VariableEntry(Symbol symbol, SExp value, boolean constant,
    HelpTopic helpinfo) {
        this.symbol = symbol;
        this.value = value;
        this.isConstant = constant;
        this.helpinfo = helpinfo;
    }

    public Symbol symbol() { return symbol; }
        
}

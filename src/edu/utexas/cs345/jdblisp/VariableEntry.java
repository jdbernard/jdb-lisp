package edu.utexas.cs345.jdblisp;

/**
 * VariableEntry
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class VariableEntry {

    public final SExp value;
    public final boolean isConstant;
    public final HelpTopic helpinfo;

    public VariableEntry(SExp value) { this(value, false); }

    public VariableEntry(SExp value, boolean constant) {
        this(value, constant, null); 
    }

    public VariableEntry(SExp value, boolean constant, HelpTopic helpinfo) {
        this.value = value;
        this.isConstant = constant;
        this.helpinfo = helpinfo;
    }
        
}

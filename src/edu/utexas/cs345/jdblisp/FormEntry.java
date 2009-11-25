package edu.utexas.cs345.jdblisp;

/**
 * FormEntry
 * @author Jonathan Bernard(jdbernard@gmail.com)
 */
public abstract class FormEntry implements SExp {

    public final Symbol name;
    public HelpTopic helpinfo;

    public FormEntry(Symbol name, HelpTopic helpinfo) {
        this.name = name;
        this.helpinfo = helpinfo;
    }

    public abstract SExp call(SymbolTable table, Seq arguments) throws LispException;
    public abstract String display(String offset);

    public SExp eval(SymbolTable symbolTable) { return this; }

}

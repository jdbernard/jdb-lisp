package edu.utexas.cs345.jdblisp;

/**
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class TableEntry {

    public final Symbol name;
    public final Symbol[] parameters;
    public final SExp body;

    public TableEntry(Symbol name, Symbol[] parameters, SExp body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

}

package edu.utexas.cs345.jdblisp;

/**
 * Keyword
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class Keyword extends Symbol {

    public Keyword(String name) { super(name); }

    /** {@inheritdoc} */
    @Override
    public SExp eval(SymbolTable symbolTable) { return this; }

    public String display(String offset) {
        return offset + "Keyword: " + name + "\n";
    }

    @Override
    public String toString() { return ":" + name; }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null) return false;
        if (!(that instanceof Keyword)) return false;
        return this.name.equals(((Keyword) that).name);
    }
}

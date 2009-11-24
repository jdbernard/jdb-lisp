package edu.utexas.cs345.jdblisp;

/**
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class Symbol implements SExp {
    public final String name;

    public Symbol(String name) { this.name = name; }

    /** {@inheritdoc}*/
    public SExp eval(SymbolTable table) throws LispException {
        // lookup value in the symbol table
        VariableEntry ve = table.lookupVariable(this);

        // err if not defined
        if (ve == null) throw new UndefinedVariableException(this);

        return ve.value;
    }

    public String display(String offset) {
        return offset + "Symbol: " + name + "\n";
    }

    @Override
    public String toString() { return name; }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null) return false;
        if (!(that instanceof Symbol)) return false;
        return this.name.equals(((Symbol) that).name);
    }

    @Override
    public int hashCode() { return name.hashCode(); }
}

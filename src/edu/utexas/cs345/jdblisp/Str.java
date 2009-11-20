package edu.utexas.cs345.jdblisp;

/**
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class Str implements SExp {

    public final String value;

    public Str(String value) {
        this.value = value;
    }

    /** {@inheritdoc}*/
    public SExp eval(SymbolTable table) { return this; }

    public String display(String offset) {
        return offset + "Str: " + value + "\n";
    }

    @Override
    public String toString() {
        return value;
    }
}

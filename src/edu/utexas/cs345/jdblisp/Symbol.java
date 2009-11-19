package edu.utexas.cs345.jdblisp;

/**
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class Symbol implements SExp {
    public final String name;

    public Symbol(String name) {
        this.name = name;
    }

    /** {@inheritdoc}*/
    public SymbolTable eval(SymbolTable table) {
        // TODO
        return null;
    }

    public String display(String offset) {
        return offset + "Symbol: " + name + "\n";
    }
}

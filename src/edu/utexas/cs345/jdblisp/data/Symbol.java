package edu.utexas.cs345.jdblisp.data;
/**
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class Symbol implements SExp {
    public final String name;

    public Symbol(String name) {
        this.name = name;
    }

    public SExp eval(SExp sexp, SymbolTable table) {
        // TODO
        return null;
    }
}

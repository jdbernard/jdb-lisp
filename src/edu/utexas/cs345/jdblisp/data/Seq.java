package edu.utexas.cs345.jdblisp.data;
/**
 * Seq
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class Seq<T extends SExp> implements SExp {

    public final T car;
    public final Seq<T> cdr;

    public Seq(T car, Seq<T> cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    public Seq(T... elements) {
        int idx = elements.length;
        Seq last = null;
        while (idx > 1) last = new Seq(elements[--idx], last);

        this.car = elements[0];
        this.cdr = last;
    }

    public SExp eval(SExp sexp, SymbolTable table) {
        // TODO
        return null;
    }
}

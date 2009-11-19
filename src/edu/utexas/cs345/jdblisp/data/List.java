package edu.utexas.cs345.jdblisp.data;
/**
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class List implements SExp {
    public final Seq<SExp> seq;

    public List(Seq<SExp> seq) {
        this.seq = seq;
    }

    public SExp eval(SExp sexp, SymbolTable table) {
        // TODO
        return null;
    }

}

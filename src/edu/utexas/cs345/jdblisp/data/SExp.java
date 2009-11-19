package edu.utexas.cs345.jdblisp.data;
/**
 * SExp
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public interface SExp {

    SymbolTable eval(SExp sexp, SymbolTable table);
}

package edu.utexas.cs345.jdblisp;

/**
 * FormEntry
 * @author Jonathan Bernard(jdbernard@gmail.com)
 */
public interface FormEntry {
    SExp call(SymbolTable table, Seq arguments) throws LispException;
}

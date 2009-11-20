package edu.utexas.cs345.jdblisp;

/**
 * SExp
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public interface SExp {

    /**
     * Evaluate this SExp within the context of the given SymbolTable.
     * @param table The SymbolTable context for this scope.
     * @return A SymbolTable containing the reutrn value.
     */
    SymbolTable eval(SymbolTable table) throws LispException;

    String display(String offset);

}

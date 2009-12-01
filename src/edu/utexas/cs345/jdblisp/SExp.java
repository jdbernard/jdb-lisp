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
    SExp eval(SymbolTable table) throws LispException;

    String display(String offset);

    public static final SExp T = new SExp() {
        public SExp eval(SymbolTable table) { return this; }
        public String display(String offset) { return offset + "T\n"; }
        public String toString() { return "T"; }
    };

    public static final SExp NIL = new SExp() {
        public SExp eval(SymbolTable table) { return this; }
        public String display(String offset) { return offset + "NIL\n"; }
        public String toString() { return "NIL"; }
    };

}

package edu.utexas.cs345.jdblisp;

/**
 * TypeException
 * @author Jonathan Bernard (jdbernard@gmail.com)
 * Represents type errors.
 */
public class TypeException extends LispException {

    public TypeException(SExp sexp, Class<? extends SExp> expectedType) {
        super("TYPE-ERROR: The value " + sexp.toString() + " is not of type "
            + expectedType.getSimpleName());
    }
}

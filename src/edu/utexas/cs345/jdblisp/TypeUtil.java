package edu.utexas.cs345.jdblisp;

/**
 * TypeUtil
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class TypeUtil {

    /**
     * Attempt to perform a LISP type conversion and create an appropriate
     * error if the type conversion fails.
     * @param desiredClass The class to cast to.
     * @param value The value to cast.
     * @return The value cast to the desired class.
     * @throws {@link edu.utexas.cs345.jdblisp.TypeException} if the cast fails.
     */
    static <T extends SExp> T attemptCast(Class<T> desiredClass, SExp value)
    throws TypeException {
        try { return desiredClass.cast(value); }
        catch (ClassCastException cce) {
            throw new TypeException(value, desiredClass);
        }
    }
}

package edu.utexas.cs345.jdblisp;

/**
 * FunctionEntry
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class FunctionEntry implements FormEntry {

    protected final Symbol[] parameters;
    protected final SExp body;

    public FunctionEntry(Symbol[] parameters, SExp body) {
        this.parameters = parameters;
        this.body = body;
    }

    public SExp call(SymbolTable symbolTable, Seq arguments) throws LispException {

        // bind arguments to parameters
        SymbolTable localScope = new SymbolTable(symbolTable);
        int i = 0;
        while (i < parameters.length) {

            // too few arguments
            if (arguments == null)
                throw new InvalidArgumentQuantityException(
                    parameters.length, i);

            // bind one arg to param
            localScope.bind(parameters[i], new VariableEntry(arguments.car.eval(symbolTable)));

            arguments = arguments.cdr;
            ++i;
        }

        // too many arguments
        if (arguments != null)
            throw new InvalidArgumentQuantityException(parameters.length,
                (i + arguments.length()));

        return body.eval(localScope);
    }
}

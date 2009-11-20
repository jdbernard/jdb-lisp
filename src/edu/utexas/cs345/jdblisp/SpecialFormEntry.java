package edu.utexas.cs345.jdblisp;

import java.util.ArrayList;

/**
 * SpecialFormEntry
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public abstract class SpecialFormEntry extends FunctionEntry {

    protected Lisp environment;

    public SpecialFormEntry(Lisp environment) { this.environment = environment; }

    public abstract SExp call(SymbolTable symbolTable, Seq arguments) throws LispException;


    public static void defineSpecialForms(Lisp environment) {
    
        SpecialFormEntry DEFUN = new SpecialFormEntry(environment) {
            public SExp call(SymbolTable symbolTable, Seq arguments) throws LispException {

                Symbol functionName;
                ArrayList<Symbol> parameters;
                SExp body;

                // TODO: check to see if a function for this symbol exists
                // and warn if so

                if (arguments.length() != 3)
                    new InvalidArgumentQuantityException(3, arguments.length());

                // first argument: Symbol for function name
                if (!(arguments.car instanceof Symbol))
                    throw new TypeException(arguments.car, Symbol.class);

                functionName = (Symbol) arguments.car;

                // second argument, parameter list
                arguments = arguments.cdr;
                assert (arguments != null);
                
                if (!(arguments.car instanceof List))
                    // TODO: error, need parameter list

                // read parameters
                parameters = new ArrayList<Symbol>();
                Seq paramSeq = ((List) arguments.car).seq;
                while (seq != null) {
                    if (!(seq.car instanceof Symbol))
                        throw new TypeException(seq.car, Symbol.class);

                    parameters.add((Symbol) seq.car);
                    seq = seq.cdr;
                }

                // third argument: function body
                arguments = arguments.cdr;
                assert (arguments != null);

                // TODO: necessary? if (!(arguments.car instanceof List))
                
                body = arguments.car;

                environment.globalSymbolTable.define(functionName,
                    new FunctionEntry(parameters.toArray(new Symbol[]{}), body));

                return functionName;
            }
        };

        SpecialFormEntry SETQ = new SpecialFormEntry(environment) {
            public SExp call(SymbolTable symbolTable, Seq arguments) throws LispException {
                
                Symbol variableName;
                SExp variableValue;

                // TODO: check for redifinition of variable (and warn)

                if (arguments.length() != 2)
                    throw new InvalidArgumentQuantityException(2,
                        arguments.length());

                // first argument: Symbol for variable name
                if (!(arguments.car instanceof Symbol))
                    // TODO: error: expected symbol

                variableName = (Symbol) arguments.car;

                // second argument: variable value
                arguments = arguments.cdr;
                assert (arguments != null);

                variableValue = arguments.car;
            }
        };

        environment.globalSymbolTable.defin(new Symbol("DEFUN"), DEFUN);
        environment.globalSymbolTable.defin(new Symbol("SETQ"), SETQ);
    }
}

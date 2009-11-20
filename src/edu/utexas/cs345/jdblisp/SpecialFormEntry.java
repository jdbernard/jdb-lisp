package edu.utexas.cs345.jdblisp;

import java.util.ArrayList;

/**
 * SpecialFormEntry
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public abstract class SpecialFormEntry implements FormEntry {

    protected Lisp environment;

    public SpecialFormEntry(Lisp environment) { this.environment = environment; }

    public abstract SExp call(SymbolTable symbolTable, Seq arguments) throws LispException;


    public static void defineSpecialForms(Lisp environment) {
    
        SpecialFormEntry DEFUN = new SpecialFormEntry(environment) {
            public SExp call(SymbolTable symbolTable, Seq arguments) throws LispException {

                Symbol functionName;
                ArrayList<Symbol> parameters = new ArrayList<Symbol>();
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
                
                //if (!(arguments.car instanceof List))
                    // TODO: error, need parameter list

                // read parameters
                Seq paramSeq = ((List) arguments.car).seq;
                while (paramSeq != null) {
                    if (!(paramSeq.car instanceof Symbol))
                        throw new TypeException(paramSeq.car, Symbol.class);

                    parameters.add((Symbol) paramSeq.car);
                    paramSeq = paramSeq.cdr;
                }

                // third argument: function body
                arguments = arguments.cdr;
                assert (arguments != null);

                // TODO: necessary? if (!(arguments.car instanceof List))
                
                body = arguments.car;

                environment.globalSymbolTable.bind(functionName,
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
                //if (!(arguments.car instanceof Symbol))
                    // TODO: error: expected symbol

                variableName = (Symbol) arguments.car;

                // second argument: variable value
                arguments = arguments.cdr;
                assert (arguments != null);

                variableValue = arguments.car;

                environment.globalSymbolTable.bind(variableName,
                    new VariableEntry(variableValue));

                return variableValue;
            }
        };

        SpecialFormEntry SUM = new SpecialFormEntry(environment) {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num sum = new Num("0");

                // variable number of arguments [0..inf)
                while (arguments != null) {
                    try {
                        sum = sum.add((Num) arguments.car.eval(symbolTable));
                    } catch (ClassCastException cce) {
                        throw new TypeException(arguments.car, Num.class);
                    }
                    arguments = arguments.cdr;
                }

                return sum;
            }
        };

        SpecialFormEntry DIF = new SpecialFormEntry(environment) {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num difference = new Num("0");

                // need at least one argument
                if (arguments == null)
                    throw new InvalidArgumentQuantityException(1, 0);

                // case: only one argument: 0 - arg
                try {
                    difference = difference.subtract(
                        (Num) arguments.car.eval(symbolTable));
                } catch (ClassCastException cce) {
                    throw new TypeException(arguments.car, Num.class);
                }

                arguments = arguments.cdr;
                if (arguments == null) return difference;

                // case: (- x y1 ... yn)
                difference = difference.negate();

                // variable number of arguments [0..inf)
                while (arguments != null)  {
                    try {
                        difference = difference.subtract(
                            (Num) arguments.car.eval(symbolTable));
                    } catch (ClassCastException cce) {
                        throw new TypeException(arguments.car, Num.class);
                    }
                    arguments = arguments.cdr;
                }

                return difference;
            }
        };

        SpecialFormEntry MUL = new SpecialFormEntry(environment) {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num product = new Num("1");

                // variable number of arguments [0..inf)
                while (arguments != null) {
                    try {
                        product = product.multiply(
                            (Num) arguments.car.eval(symbolTable));
                    } catch (ClassCastException cce) {
                        throw new TypeException(arguments.car, Num.class);
                    }
                    arguments = arguments.cdr;
                }

                return product;
            }
        };

        SpecialFormEntry DIV = new SpecialFormEntry(environment) {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num dividend = new Num("1");
                Num firstArg;

                if (arguments == null)
                    throw new InvalidArgumentQuantityException(1, 0);

                // case: only one argument: 1 / arg
                try { firstArg = (Num) arguments.car.eval(symbolTable); }
                catch (ClassCastException cce) {
                    throw new TypeException(arguments.car, Num.class);
                }

                dividend = dividend.divideBy(firstArg);

                arguments = arguments.cdr;
                if (arguments == null) return dividend;

                // case: (/ x y1 ... yn)
                dividend = firstArg;

                // variable number of arguments [0..inf)
                while (arguments != null) {
                    try {
                        dividend = dividend.divideBy(
                            (Num) arguments.car.eval(symbolTable));
                    } catch (ClassCastException cce) {
                        throw new TypeException(arguments.car, Num.class);
                    }
                    arguments = arguments.cdr;
                }

                return dividend;
            }
        };

        environment.globalSymbolTable.bind(new Symbol("DEFUN"), DEFUN);
        environment.globalSymbolTable.bind(new Symbol("SETQ"), SETQ);
        environment.globalSymbolTable.bind(new Symbol("+"), SUM);
        environment.globalSymbolTable.bind(new Symbol("-"), DIF);
        environment.globalSymbolTable.bind(new Symbol("*"), MUL);
        environment.globalSymbolTable.bind(new Symbol("/"), DIV);
    }
}

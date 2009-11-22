package edu.utexas.cs345.jdblisp;

import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * SpecialFormEntry
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public abstract class SpecialFormEntry implements FormEntry {

    public final HelpTopic helpinfo;

    protected LISPRuntime environment;

    public SpecialFormEntry(LISPRuntime environment, HelpTopic helpinfo) {
        this.environment = environment;
        this.helpinfo = helpinfo;
    }

    public HelpTopic helpinfo() { return helpinfo; }

    public abstract SExp call(SymbolTable symbolTable, Seq arguments)
        throws LispException;

    // ------------------------
    // SPECIAL FORMS DEFINITION
    // ------------------------

    // JDB-Lisp includes on-line help for all of its special forms. See the
    // help strings for documentation of the individual special forms.
    
    /**
     * TODO
     */
    public static void defineSpecialForms(LISPRuntime environment) {

        // ----
        // HELP
        // ----

        SpecialFormEntry HELP = new SpecialFormEntry(
            environment,
            new FormHelpTopic("HELP",
                "Display online help information for a topic.",
                "(help <topic>)",
                null,
                "topic",
                    "either a string representing the topic to lookup or a symbol"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                
                HelpTopic topic = null;

                // no arguments: print help for HELP
                if (arguments == null)
                    return this.call(symbolTable,
                        new Seq(new Symbol("HELP"), null));

                // too many arguments
                if (arguments.length() > 1)
                    throw new InvalidArgumentQuantityException(1,
                        arguments.length());

                // try to find the topic or function help
                if (arguments.car instanceof Str) {
                    topic = HelpTopic.helpTopics.get(
                        ((Str) arguments.car).value);
                } else if (arguments.car instanceof Symbol) {
                    try {
                        FormEntry fe = symbolTable.lookupFunction(
                            (Symbol) arguments.car);
                        topic = fe.helpinfo();
                    } catch (LispException le) { topic = null; }
                }

                // no topic found
                if (topic == null)  {
                    new PrintWriter(environment.getOutputStream(), true)
                        .println(
                            "No help information found for topic '"
                            + arguments.car.toString() + "'.");
                    return null;
                }

                topic.print(environment.getOutputStream());
                return null;
            }
        };

        // -----
        // DEFUN
        // -----

        SpecialFormEntry DEFUN = new SpecialFormEntry(
            environment,
            new FormHelpTopic("DEFUN", "Define a (global) function.",
                "(defun <name> (<param-list>) <func-body>)",
                "Create a function binding. This will replace any existing binding.",
                "name", "the symbol to bind to the function definition.",
                "param-list", "a list of symbols that will be bound in the "
                    + "function scope to the arguments passed to the function.",
                "func-body", "an sexpression evaluated when the function is "
                    + "called."))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Symbol functionName;
                ArrayList<Symbol> parameters = new ArrayList<Symbol>();
                SExp body;

                // TODO: check to see if a function for this symbol exists
                // and warn if so

                if (arguments == null || arguments.length() != 3)
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
                    new FunctionEntry(functionName,
                        parameters.toArray(new Symbol[]{}),
                        body));

                return functionName;
            }
        };

        // ----
        // SETQ
        // ----


        SpecialFormEntry SETQ = new SpecialFormEntry(
            environment,
            new FormHelpTopic("SETQ", "Define a global variable.",
                "(setq <name> <value>)",
                "Bind a value to a symbol in the global symbol table.",
                "name", "the symbol to bind to the given value.",
                "value", "an sexpression representing the value of the "
                    + "variable. The value of the variable when it is "
                    + "evaluated is the evaluated value of this sexpression."))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                
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

                variableValue = arguments.car.eval(symbolTable);

                environment.globalSymbolTable.bind(variableName,
                    new VariableEntry(variableValue));

                return variableValue;
            }
        };

        // -----
        // TRACE
        // -----

        SpecialFormEntry TRACE = new SpecialFormEntry(
            environment,
            new FormHelpTopic("TRACE",
                "enable trace information for a function",
                "(trace <funcname>)",
                "Turn on trace information for a function.",
                "funcname", "the name of the function to trace"))
         {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                if (arguments == null || arguments.car == null)
                    return null;

                if (!(arguments.car instanceof Symbol))
                    throw new LispException(arguments.car.toString()
                        + " is not a valid function name.");

                FormEntry fe = symbolTable.lookupFunction((Symbol) arguments.car);

                if (fe instanceof FunctionEntry) ((FunctionEntry) fe).enableTrace(true);
                // TODO: else throw error

                return null;
            }
         };
        // ---
        // SUM
        // ---

        SpecialFormEntry SUM = new SpecialFormEntry(
            environment,
            new FormHelpTopic("+", "Sum several expressions.",
                "(+ [<addend_1> ... <addend_n>])",
                "Compute the summation of the zero or more expressions passed"
                    + "as arguments. In general, expressions are evaluated "
                    + "before being bound to function parameters. The"
                    + " expressions passed to sum must evaluate to numbers.",
                "addend_1 ... addend_n", "Addends may be any expression that "
                    + "evaluates to a number."))
        {
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

        SpecialFormEntry DIF = new SpecialFormEntry(
            environment,
            new FormHelpTopic("-", "Subtract several expressions.",
                "(- subtrahend) | (- ??? <subtrahend_1> [... <subtrahend_n>])",
                "Perform a subtraction. If there is only one argument passed "
                    + "then result = 0 - arg. If multiple arguments are passed"
                    + " then result = arg_1 - arg_2 - ... - args_n. In "
                    + "general, expressions are evaluated before being bound "
                    + " to function parameters. The expressions passed to - "
                    + "must evaluate to numbers.",
                "???", "In the case of multiple arguments to -, this is the "
                    + "number from which the others are subtracted.",
                "subtrahend_1 ... subtrahend_n", "Subtrahends are numbers "
                    + "subtracted from the ??? and may be any expression that "
                    + "evaluates to a number."))
        {
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

        SpecialFormEntry MUL = new SpecialFormEntry(
            environment,
            new FormHelpTopic("*", "Multiply several expressions.",
                "(+ [<multiplicand_1> ... <multiplicand_n>])",
                "Compute the product of the zero or more expressions passed"
                    + "as arguments. In general, expressions are evaluated "
                    + "before being bound to function parameters. The"
                    + " expressions passed to multiply must evaluate to numbers.",
                "multiplicand_1 ... multiplicand_n", "Multiplicands may be "
                    + "any expression that evaluates to a number."))
        {
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

        SpecialFormEntry DIV = new SpecialFormEntry(
            environment,
            new FormHelpTopic("/", "Divide several expressions.",
                "(- divisor) | (- quotient <divisor_1> [... <divisor_n>])",
                "Perform division. If there is only one argument passed "
                    + "then result = 1/ arg. If multiple arguments are passed"
                    + " then result = arg_1 / arg_2 / ... / args_n, computed "
                    + "from left to right. In general, expressions are "
                    + "evaluated before being bound  to function parameters. "
                    + "The expressions passed to / must evaluate to numbers.",
                "quotient", "In the case of multiple arguments to /, this is "
                    + "the number which is diveded.",
                "divisor_1 ... divisor_n", "Divisors are the numbers dividing "
                    + "the quotient and may be any expression that evaluates "
                    + "to a number."))
        {
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

        SpecialFormEntry ENABLEDEBUGAST = new SpecialFormEntry(
            environment,
            new FormHelpTopic("ENABLE-DEBUG-AST",
                "Enable debug information: abstract syntax tree.",
                "(enable-debug-ast [<enable>])",
                "When DEBUG-AST is enabled, the runtime environment prints a "
                    + "representation of the abstract syntax tree generated "
                    + "by the parser for each sexpression it parses.",
                "enable", "NIL = disabled, anything else = enabled. No "
                    + "argument = enabled."))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                if (arguments == null) {
                    environment.dumpAST = true;
                    return null;
                }

                SExp retVal = arguments.car.eval(symbolTable);

                if (retVal != null) environment.dumpAST = true;
                else environment.dumpAST = false;

                return retVal;
            }
        };

        /*SpecialFormEntry LET = new SpecialFormEntry(environment) {
            public SExp call(SymbolTable table, Seq arguments) {
                // TODO
            }
        }*/

        environment.globalSymbolTable.bind(new Symbol("HELP"), HELP);
        environment.globalSymbolTable.bind(new Symbol("DEFUN"), DEFUN);
        environment.globalSymbolTable.bind(new Symbol("SETQ"), SETQ);
            environment.globalSymbolTable.bind(new Symbol("TRACE"), TRACE);
        environment.globalSymbolTable.bind(new Symbol("+"), SUM);
        environment.globalSymbolTable.bind(new Symbol("-"), DIF);
        environment.globalSymbolTable.bind(new Symbol("*"), MUL);
        environment.globalSymbolTable.bind(new Symbol("/"), DIV);
        environment.globalSymbolTable.bind(new Symbol("ENABLE-DEBUG-AST"), ENABLEDEBUGAST);
    }
}

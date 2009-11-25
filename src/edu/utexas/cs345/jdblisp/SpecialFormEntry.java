package edu.utexas.cs345.jdblisp;

import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * SpecialFormEntry
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public abstract class SpecialFormEntry extends FormEntry {

    protected LISPRuntime environment;
    protected int expectedArgumentCount;
    protected Class<? extends SExp>[] expectedArgumentTypes;

    public SpecialFormEntry(Symbol name, LISPRuntime environment,
    HelpTopic helpinfo, int expectedArgCount, Class<? extends SExp>... expectedArgTypes) {
        super(name, helpinfo);

        // three cases: no arguments, so EAT == null || 0; arg types homogenous
        // so EAT == 1; arg types heterogenous, EAT == numArgs
        assert (expectedArgumentTypes == null ||
                expectedArgumentTypes.length <= 1 ||
                expectedArgumentTypes.length == expectedArgumentCount);

        this.environment = environment;
        this.expectedArgumentCount = expectedArgCount;
        this.expectedArgumentTypes = expectedArgTypes;
    }

    public abstract SExp call(SymbolTable symbolTable, Seq arguments)
        throws LispException;

    @Override
    public String display(String offset) {
        return offset + "Special Form Entry: " + name.toString();
    }

    @Override
    public String toString() {
        return "<SPECIAL-FORM (" + name.toString() + ") >";
    }

    protected void checkArguments(Seq arguments) throws LispException {

      boolean argsUnbounded = expectedArgumentCount < 0;
      int expectedArgs = Math.abs(expectedArgumentCount);
      int actualArgs;

      // first case: expect 0 arguments
      if (expectedArgs == 0) {
        if (arguments != null)
          throw new InvalidArgumentQuantityException(0, arguments.length());
        return;
      }

      // expect at least one arg, err if none given
      if (arguments == null)
        throw new InvalidArgumentQuantityException(expectedArgs, 0);

      actualArgs = arguments.length();

      // there should be at least as many actual argument as expected
      if ( actualArgs < expectedArgs ||
          (actualArgs > expectedArgs && !argsUnbounded)) {
          if (argsUnbounded)
            throw new InvalidArgumentQuantityException("expected at least "
                + expectedArgs + " arguments");
          else 
            throw new InvalidArgumentQuantityException(expectedArgs, actualArgs);
      }

      assert (expectedArgumentTypes != null &&
              expectedArgumentTypes.length > 0);

      // at this point, we know that the amount of arguments is valid
      for (int i = 0; arguments != null; ++i) {
        Class<? extends SExp> expectedType =
            (i >= expectedArgumentTypes.length ?
                expectedArgumentTypes[expectedArgumentTypes.length - 1] :
                expectedArgumentTypes[i]);

        // check the type of the argument                            
        if (!expectedType.isAssignableFrom(arguments.car.getClass()))
          throw new TypeException(arguments.car, expectedType);

        // next argument
        arguments = arguments.cdr;
      }

      return; // no error, good times
    }
    // ------------------------
    // SPECIAL FORMS DEFINITION
    // ------------------------

    // JDB-Lisp includes on-line help for all of its special forms. See the
    // help strings for documentation of the individual special forms.
    
    /**
     * TODO
     */
    public static void defineSpecialForms(LISPRuntime environment) {

        // ---
        // LTE
        // ---

        final SpecialFormEntry LTE = new SpecialFormEntry(
            new Symbol("<="),
            environment,
            new FormHelpTopic("<=", "Less than or equal to",
                "(<= <number>*) => <result>",
                "The value of <= is true if the numbers are in monotonically "
                    + "nondecreasing order; otherwise it is false.",
                "number", "a real",
                "result", "a boolean"),
            -2, Num.class)
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num current;
                Num next;

                checkArguments(arguments);

                // get first number
                current = (Num) arguments.car;

                // advance to next argument
                arguments = arguments.cdr;

                while(arguments != null) {
                    // get next number
                    next = (Num) arguments.car;

                    // current > next, return false
                    if (current.compareTo(next) > 0) return SExp.NIL;

                    // next becomes current
                    current = next;

                    // advance to next argument
                    arguments = arguments.cdr;
                }

                // all are nondecreasing, return true
                return SExp.T;
            }
        };

        // --
        // LT
        // --

        final SpecialFormEntry LT = new SpecialFormEntry(
            new Symbol("<"),
            environment,
            new FormHelpTopic("<", "Less than",
                "(< <number>*) => <result>",
                "The value of < is true if the numbers are in monotonically "
                    + "increasing order; otherwise it is false.",
                "number", "a real",
                "result", "a boolean"),
            -2, Num.class)
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num current;
                Num next;

                checkArguments(arguments);

                // get first number
                current = (Num) arguments.car;

                // advance to next argument
                arguments = arguments.cdr;

                while(arguments != null) {
                    // get next number
                    next = (Num) arguments.car;

                    // current >= next, return false
                    if (current.compareTo(next) >= 0) return SExp.NIL;

                    // next becomes current
                    current = next;

                    // advance to next argument
                    arguments = arguments.cdr;
                }

                // all are increasing, return true
                return SExp.T;
            }
        };

        // ---------
        // NUMEQ (=) 
        // ---------

        final SpecialFormEntry NUMEQ = new SpecialFormEntry(
            new Symbol("="),
            environment,
            new FormHelpTopic("=", "Equal to",
                "(= <number>*) => <result>",
                "The value of = is true if all numbers are the same in value.",
                "number", "a number",
                "result", "a boolean"),
            -2, Num.class)
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num current;
                Num next;

                checkArguments(arguments);

                // get first number
                current = (Num) arguments.car;

                // advance to next argument
                arguments = arguments.cdr;

                while(arguments != null) {
                    // get next number
                    next = (Num) arguments.car;

                    // current != next, return false
                    if (current.compareTo(next) != 0) return SExp.NIL;

                    // next becomes current
                    current = next;

                    // advance to next argument
                    arguments = arguments.cdr;
                }

                // all are equal, return true
                return SExp.T;
            }
        };

        // -------------
        // NUMNOTEQ (/=) 
        // -------------

        final SpecialFormEntry NUMNOTEQ = new SpecialFormEntry(
            new Symbol("/="),
            environment,
            new FormHelpTopic("/=", "Not equal to",
                "(/= <number>*) => <result>",
                "The value of /= is true if no two numbers are the same in value.",
                "number", "a number",
                "result", "a boolean"),
            -2, Num.class)
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num current;
                Num next;

                checkArguments(arguments);

                // get first number
                current = (Num) arguments.car;

                // advance to next argument
                arguments = arguments.cdr;

                while(arguments != null) {
                    // get next number
                    next = (Num) arguments.car;

                    // current == next, return false
                    if (current.compareTo(next) == 0) return SExp.NIL;

                    // next becomes current
                    current = next;

                    // advance to next argument
                    arguments = arguments.cdr;
                }

                // all are non-equal, return true
                return SExp.T;
            }
        };

        // --
        // GT
        // --

        final SpecialFormEntry GT = new SpecialFormEntry(
            new Symbol(">"),
            environment,
            new FormHelpTopic(">", "Greater than",
                "(> <number>*) => <result>",
                "The value of > is true if the numbers are in monotonically "
                    + "decreasing order; otherwise it is false.",
                "number", "a number",
                "result", "a boolean"),
            -2, Num.class)
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num current;
                Num next;

                checkArguments(arguments);

                // get first number
                current = (Num) arguments.car;

                // advance to next argument
                arguments = arguments.cdr;

                while(arguments != null) {
                    // get next number
                    next = (Num) arguments.car;

                    // current <= next, return false
                    if (current.compareTo(next) <= 0) return SExp.NIL;

                    // next becomes current
                    current = next;

                    // advance to next argument
                    arguments = arguments.cdr;
                }

                // all are decreasing, return true
                return SExp.T;
            }
        };

        // ---
        // GTE
        // ---

        final SpecialFormEntry GTE = new SpecialFormEntry(
            new Symbol(">="),
            environment,
            new FormHelpTopic(">=", "Greater than or equal to",
                "(>= <number>*) => <result>",
                "The value of > is true if the numbers are in monotonically "
                    + "non-increasing order; otherwise it is false.",
                "number", "a number",
                "result", "a boolean"),
            -2, Num.class)
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num current;
                Num next;

                checkArguments(arguments);

                // get first number
                current = (Num) arguments.car;

                // advance to next argument
                arguments = arguments.cdr;

                while(arguments != null) {
                    // get next number
                    next = (Num) arguments.car;

                    // current < next, return false
                    if (current.compareTo(next) < 0) return SExp.NIL;

                    // next becomes current
                    current = next;

                    // advance to next argument
                    arguments = arguments.cdr;
                }

                // all are non-increasing, return true
                return SExp.T;
            }
        };

        // ---
        // DIV
        // ---

        final SpecialFormEntry DIV = new SpecialFormEntry(
            new Symbol("/"),
            environment,
            new FormHelpTopic("/", "Divide several expressions.",
                "(- divisor) | (- dividend <divisor_1> [... <divisor_n>])",
                "Perform division. If there is only one argument passed "
                    + "then result = 1/ arg. If multiple arguments are passed"
                    + " then result = arg_1 / arg_2 / ... / args_n, computed "
                    + "from left to right. In general, expressions are "
                    + "evaluated before being bound  to function parameters. "
                    + "The expressions passed to / must evaluate to numbers.",
                "dividend", "In the case of multiple arguments to /, this is "
                    + "the number which is diveded.",
                "divisor_1 ... divisor_n", "Divisors are the numbers dividing "
                    + "the dividend and may be any expression that evaluates "
                    + "to a number."),
            -1, Num.class)
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num dividend = new Num("1");
                Num firstArg;

                checkArguments(arguments);

                // case: only one argument: 1 / arg
                firstArg = (Num) arguments.car.eval(symbolTable);

                dividend = dividend.divideBy(firstArg);

                arguments = arguments.cdr;
                if (arguments == null) return dividend;

                // case: (/ x y1 ... yn)
                dividend = firstArg;

                // variable number of arguments [0..inf)
                while (arguments != null) {
                    dividend = dividend.divideBy(
                        (Num) arguments.car.eval(symbolTable));
                    arguments = arguments.cdr;
                }

                return dividend;
            }
        };

        // ---
        // DIF
        // ---

        final SpecialFormEntry DIF = new SpecialFormEntry(
            new Symbol("-"),
            environment,
            new FormHelpTopic("-", "Subtract several expressions.",
                "(- subtrahend) | (- <minuend> <subtrahend_1> [... <subtrahend_n>])",
                "Perform a subtraction. If there is only one argument passed "
                    + "then result = 0 - arg. If multiple arguments are passed"
                    + " then result = arg_1 - arg_2 - ... - args_n. In "
                    + "general, expressions are evaluated before being bound "
                    + " to function parameters. The expressions passed to - "
                    + "must evaluate to numbers.",
                "minuend", "In the case of multiple arguments to -, this is "
                    + "the number from which the others are subtracted.",
                "subtrahend_1 ... subtrahend_n", "Subtrahends are numbers "
                    + "subtracted from the minuend and may be any expression "
                    + "that evaluates to a number."),
            -1, Num.class)
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num difference = new Num("0");

                checkArguments(arguments);

                // case: only one argument: 0 - arg
                difference = difference.subtract(
                    (Num) arguments.car.eval(symbolTable));

                arguments = arguments.cdr;
                if (arguments == null) return difference;

                // case: (- x y1 ... yn)
                difference = difference.negate();

                // variable number of arguments [0..inf)
                while (arguments != null)  {
                    difference = difference.subtract(
                        (Num) arguments.car.eval(symbolTable));
                    arguments = arguments.cdr;
                }

                return difference;
            }
        };

        // ---
        // MUL
        // ---

        final SpecialFormEntry MUL = new SpecialFormEntry(
            new Symbol("*"),
            environment,
            new FormHelpTopic("*", "Multiply several expressions.",
                "(+ [<multiplicand_1> ... <multiplicand_n>])",
                "Compute the product of the zero or more expressions passed"
                    + "as arguments. In general, expressions are evaluated "
                    + "before being bound to function parameters. The"
                    + " expressions passed to multiply must evaluate to numbers.",
                "multiplicand_1 ... multiplicand_n", "Multiplicands may be "
                    + "any expression that evaluates to a number."),
            -1, Num.class)  // not technically correct, * accepts 0 arguments
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

        // ---
        // SUM
        // ---

        final SpecialFormEntry SUM = new SpecialFormEntry(
            new Symbol("+"),
            environment,
            new FormHelpTopic("+", "Sum several expressions.",
                "(+ [<addend_1> ... <addend_n>])",
                "Compute the summation of the zero or more expressions passed"
                    + "as arguments. In general, expressions are evaluated "
                    + "before being bound to function parameters. The"
                    + " expressions passed to sum must evaluate to numbers.",
                "addend_1 ... addend_n", "Addends may be any expression that "
                    + "evaluates to a number."),
            -1, Num.class)  // not technically correct, + accepts 0 arguments
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

        // ---
        // CAR
        // ---

        // TODO

        // ----
        // CONS
        // ----

        final SpecialFormEntry CONS = new SpecialFormEntry(
            new Symbol("CONS"),
            environment,
            new FormHelpTopic("CONS", "create a cons",
                "(cons <object-1> <object-2>) => <cons>",
                "Creates a fresh cons, the car of which is object-1 and the "
                    + "cdr of which is object-2.",
                "object-1", "an object",
                "object-2", "an object",
                "cons", "a cons"),
            2, SExp.class)
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                
                SExp object1, object2;
                Cons cons;

                checkArguments(arguments);

                // get the two objects
                object1 = arguments.car.eval(symbolTable);
                object2 = arguments.cdr.car.eval(symbolTable);

                if (object2 instanceof List) cons = new Cons(object1, (List) object2);
                else cons = new Cons(object1, object2);

                return new List(cons);
            }
        };

        // -----
        // DEFUN
        // -----

        final SpecialFormEntry DEFUN = new SpecialFormEntry(
            new Symbol("DEFUN"),
            environment,
            new FormHelpTopic("DEFUN", "Define a (global) function.",
                "(defun <name> (<param-list>) <func-body>)",
                "Create a function binding. This will replace any existing binding.",
                "name", "the symbol to bind to the function definition.",
                "param-list", "a list of symbols that will be bound in the "
                    + "function scope to the arguments passed to the function.",
                "func-body", "an sexpression evaluated when the function is "
                    + "called."),
            3, Symbol.class, List.class, SExp.class)
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Symbol functionName;
                ArrayList<Symbol> parameters = new ArrayList<Symbol>();
                SExp body;

                checkArguments(arguments);

                // TODO: check to see if a function for this symbol exists
                // and warn if so

                // first argument: Symbol for function name
                functionName = (Symbol) arguments.car;

                // second argument, parameter list
                arguments = arguments.cdr;
                assert (arguments != null);
                
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

        // ------------
        // DEFPARAMETER
        // ------------

        final SpecialFormEntry DEFPARAMETER = new SpecialFormEntry(
            new Symbol("DEFPARAMETER"),
            environment,
            new FormHelpTopic("DEFPARAMETER", "define a dynamic variable",
                "(defparameter <name> [<initial-value> [<documentation>]]) => <name>",
                "defparameter establishes name as a dynamic variable. "
                    + "defparameter unconditionally assigns the initial-value "
                    + "to the dynamic variable named name (as opposed to "
                    + "defvar, which assigns initial-value (if supplied) to "
                    + "the dynamic variable named name only if name is not "
                    + "already bound.)",
                "name", "a symbol; not evaluated. ",
                "initial-value", "a form, always evaluated",
                "documentation", "a string; not evaluated."),
            -1, Symbol.class, SExp.class, Str.class)
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Symbol name;
                SExp initValue = null;
                HelpTopic helpinfo = null;

                checkArguments(arguments);

                // first argument: variable name
                name = (Symbol) arguments.car;

                // second argument: initial value
                arguments = arguments.cdr;
                if (arguments != null) {
                    initValue = arguments.car.eval(symbolTable);
                
                    // third argument: documentation
                    arguments = arguments.cdr;
                    if (arguments != null) 
                        helpinfo = new HelpTopic(name.toString(), "variable", 
                            ((Str) arguments.car).value);
                }

                symbolTable.bind(name,
                    new VariableEntry(initValue, false, helpinfo));

                return name;

            }
        };

        // ------
        // DEFVAR
        // ------

        final SpecialFormEntry DEFVAR = new SpecialFormEntry(
            new Symbol("DEFVAR"),
            environment,
            new FormHelpTopic("DEFVAR", "define a variable",
                "(defvar <name> [<initial-value> [<documentation>]]) => <name>",
                "defvar establishes name as a dynamic variable and assigns "
                    + "initial-value (if supplied) to the dynamic variable "
                    + "named name only if name is not already bound (this is"
                    + " in contrast to defparameter, which does not case if "
                    + "the name has already been bound. ",
                "name", "a symbol; not evaluated. ",
                "initial-value", "a form, evaluated only if name is not "
                    + "already bound.",
                "documentation", "a string; not evaluated."),
            -1, Symbol.class, SExp.class, Str.class)
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                
                Symbol name;
                SExp initValue = null;
                HelpTopic helpinfo = null;

                checkArguments(arguments);

                // first argument: variable name
                name = (Symbol) arguments.car;

                // if this variable is already defined, return without
                // setting it
                if (symbolTable.lookupVariable(name) != null)
                    return arguments.car;

                return DEFPARAMETER.call(symbolTable, arguments);
            }
        };

        // ----------------
        // ENABLE-DEBUG-AST
        // ----------------

        final SpecialFormEntry ENABLEDEBUGAST = new SpecialFormEntry(
            new Symbol("ENABLE-DEBUG-AST"),
            environment,
            new FormHelpTopic("ENABLE-DEBUG-AST",
                "Enable debug information: abstract syntax tree.",
                "(enable-debug-ast [<enable>])",
                "When DEBUG-AST is enabled, the runtime environment prints a "
                    + "representation of the abstract syntax tree generated "
                    + "by the parser for each sexpression it parses.",
                "enable", "NIL = disabled, anything else = enabled. No "
                    + "argument = enabled."), -1)
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                if (arguments == null) {
                    environment.dumpAST = true;
                    return SExp.NIL;
                }

                SExp retVal = arguments.car.eval(symbolTable);

                if (retVal != null && retVal != SExp.NIL) environment.dumpAST = true;
                else environment.dumpAST = false;

                return retVal;
            }
        };

        // --------
        // FUNCTION
        // --------

        final SpecialFormEntry FUNCTION = new SpecialFormEntry(
            new Symbol("FUNCTION"),
            environment,
            new FormHelpTopic("FUNCTION", "retrieve a function",
                "(function <func-name>) => <function>",
                "function returns the function specified by the symbol passed "
                    + " as an argument. #'funcname is equivalent to (function "
                    + " funcname).",
                "func-name", "a symbol naming a function",
                "function", "a function"),
            1, Symbol.class)
        {
            public SExp call(SymbolTable symbolTable, Seq arguments) 
            throws LispException {
                FormEntry fe = null;

                checkArguments(arguments);

                fe = symbolTable.lookupFunction((Symbol) arguments.car);

                if (fe == null)
                    throw new UndefinedFunctionException((Symbol) arguments.car);

                return fe;
            }
        };

        // -------
        // FUNCALL
        // -------

        final SpecialFormEntry FUNCALL = new SpecialFormEntry(
            new Symbol("FUNCALL"),
            environment,
            new FormHelpTopic("FUNCALL", "make a function call",
                "(funcall <function> <arg>*) => <result>",
                "funcall applies function to args. If function is a symbol, "
                    + "it is coerced to a function as if by finding its "
                    + "functional value in the global environment.",
                "function", "a function designator",
                "arg", "an object",
                "results", "the result of the function call"),
            -1, SExp.class)
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                checkArguments(arguments);

                // first argument: function designator
                SExp func = arguments.car.eval(symbolTable);
                FormEntry fe = null;
                arguments = arguments.cdr;

                // cast if already a form entry
                if (func instanceof FormEntry) fe = (FormEntry) func;

                // lookup the function if it is a symbol
                if (func instanceof Symbol) {
                    fe = environment.globalSymbolTable
                        .lookupFunction((Symbol) func);
                    if (fe == null)
                        throw new UndefinedFunctionException((Symbol) func);
                }

                if (fe == null)
                    throw new TypeException(func, FormEntry.class);

                // make the function call
                return fe.call(symbolTable, arguments);
            }
        };

        // ----
        // GETF
        // ----

        final SpecialFormEntry GETF = new SpecialFormEntry(
            new Symbol("GETF"),
            environment,
            new FormHelpTopic("GETF", "",
                "(getf <plist> <indicator> [<default>]) => <value>",
                "getf finds a property on the plist whose property indicator "
                    + "is identical to indicator, and returns its "
                    + "corresponding property value. If there are multiple "
                    + "properties with that property indicator, getf uses the "
                    + "first such property. If there is no property with that "
                    + "property indicator, default is returned.",
                "plist", "a property list.",
                "indicator", "an object",
                "default", "an object. The default is NIL",
                "value", "an object"),
            -2, List.class, SExp.class, SExp.class)
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                SExp plistEval;
                Seq plistSeq;
                SExp indicator;
                SExp retVal = SExp.NIL;

                checkArguments(arguments);

                // first argument: property list
                plistEval = arguments.car.eval(symbolTable);
                plistSeq = ((List) plistEval).seq;

                // second argument: indicator
                arguments = arguments.cdr;
                indicator = arguments.car.eval(symbolTable);

                // third argument: default value
                arguments = arguments.cdr;
                if (arguments != null)
                    retVal = arguments.car.eval(symbolTable);

                while(plistSeq != null) {

                    // check this value for equality
                    if (plistSeq.car.equals(indicator))
                        if (plistSeq.cdr != null)
                            return plistSeq.cdr.car;

                    // advance to the next pair (or terminate)
                    plistSeq = (plistSeq.cdr == null ? null : plistSeq.cdr.cdr);
                }

                return retVal;
            }
        };

        // ----
        // HELP
        // ----

        final SpecialFormEntry HELP = new SpecialFormEntry(
            new Symbol("HELP"),
            environment,
            new FormHelpTopic("HELP",
                "Display online help information for a topic.",
                "(help [<topic>*])",
                null,
                "topic",
                "either a string representing the topic to lookup or a symbol"),
            -1, SExp.class) // technically can accept 0 arguments
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                
                ArrayList<HelpTopic> topics = new ArrayList<HelpTopic>();

                // no arguments: print help for HELP
                if (arguments == null)
                    return this.call(symbolTable,
                        new Seq(new Symbol("HELP"), null));

                while (arguments != null) {
                    // try to find the topic or function help
                    if (arguments.car instanceof Str) {
                        topics.add(HelpTopic.helpTopics.get(
                            ((Str) arguments.car).value));
                    } else if (arguments.car instanceof Symbol) {

                        // lookup help for funtion
                        FormEntry fe = symbolTable.lookupFunction(
                            (Symbol) arguments.car);
                        if (fe != null) topics.add(fe.helpinfo);

                        // lookup help for variable
                        VariableEntry ve = symbolTable.lookupVariable(
                            (Symbol) arguments.car);
                        if (ve != null) topics.add(ve.helpinfo);
                    }

                    arguments = arguments.cdr;
                }

                for (HelpTopic topic : topics)
                    topic.print(environment.getOutputStream());

                return SExp.NIL;
            }
        };

        // --
        // IF
        // --

        final SpecialFormEntry IF = new SpecialFormEntry(
            new Symbol("IF"),
            environment,
            new FormHelpTopic("IF",
                "conditional code execution",
                "(if <test-form> <then-form> [<else-form>]) => result*",
                "if allows the execution of a form to be dependent on a "
                    + "single test-form. First test-form is evaluated. If "
                    + "the result is true, then then-form is selected; "
                    + "otherwise else-form is selected. Whichever form is "
                    + "selected is then evaluated.",
                "test-form", "a form.",
                "then-form", "a form.",
                "else-form", "a form. The default is nil. ",
                "results", "if the test-form yielded true, the values "
                    + "returned by the then-form; otherwise, the values "
                    + "returned by the else-form."),
            -2, SExp.class)
        {
            public SExp call(SymbolTable symbolTable, Seq arguments) 
            throws LispException {

                checkArguments(arguments);

                // evaluate test form
                SExp testResult = arguments.car.eval(symbolTable);

                // advance to then-form
                arguments = arguments.cdr;

                // if false, advance to else-form
                if (testResult == null || testResult == SExp.NIL) arguments = arguments.cdr;

                if (arguments == null) return SExp.NIL;

                return arguments.eval(symbolTable);
            }
        };

        // ------
        // LAMBDA
        // ------

        final SpecialFormEntry LAMBDA = new SpecialFormEntry(
            new Symbol("LAMBDA"),
            environment,
            new FormHelpTopic("LAMDA",
                "create an anonymous function over the current lexical closure",
                "(lambda <param-list> <form>) => <lambda>",
                "",
                "param-list", "a list of symbols",
                "form", "a form",
                "lambda", "a function"),
            2, List.class, SExp.class)
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                
                ArrayList<Symbol> parameters = new ArrayList<Symbol>();
                SExp body;
                Seq paramSeq;

                checkArguments(arguments);

                // first parameter: parameters to the lambda
                paramSeq = ((List) arguments.car).seq;
                while (paramSeq != null) {
                    if (!(paramSeq.car instanceof Symbol))
                        throw new TypeException(paramSeq.car, Symbol.class);

                    parameters.add((Symbol) paramSeq.car);
                    paramSeq = paramSeq.cdr;
                }

                // second argument: function body
                arguments = arguments.cdr;
                assert (arguments != null);

                body = arguments.car;

                return new Lambda(parameters.toArray(new Symbol[]{}),
                    body, symbolTable);
            }
        };

        // ---
        // LET
        // ---

        final SpecialFormEntry LET = new SpecialFormEntry(
            new Symbol("LET"),
            environment,
            new FormHelpTopic("LET", "create new lexical variable bindings",
                "(let (([<var> <init-form>])*) <form>*) => <result>",
                "let creates new variable bindings and executes a series of "
                    + "forms that use these bindings. let performs the "
                    + "bindings in parallel (as opposed to let* which does "
                    + "them sequentially). The form (let ((var1 init-form-1) "
                    + "(var2 init-form-2) ...  (varm init-form-m)) form1 form2 "
                    + "... formn) first evaluates the expressions init-form-1, "
                    + "init-form-2, and so on, in that order, saving the "
                    + "resulting values. Then all of the variables varj are "
                    + "bound to the corresponding values; each binding is "
                    + "lexical. The expressions formk are then evaluated in "
                    + "order; the values of all but the last are discarded "
                    + "(that is, the body of a let is an implicit progn).",
                "var", "a symbol",
                "init-form", "a form",
                "form", "a form",
                "result", "the value returned by the last form"),
            -1, List.class, SExp.class) 
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                
                SymbolTable newScope;
                Seq letBinding;
                ArrayList<Symbol> symbols = new ArrayList<Symbol>();
                ArrayList<SExp> values = new ArrayList<SExp>();
                SExp retVal = SExp.NIL;

                checkArguments(arguments);

                letBinding = ((List) arguments.car).seq;

                while (letBinding != null) {

                    // each binding should be a list of a symbol and form
                    if (!(letBinding.car instanceof List))
                        throw new LispException("Malformed LET bindings: "
                            + letBinding.car.toString());

                    // get the symbol
                    Seq binding = ((List) letBinding.car).seq;
                    if (binding == null) 
                        throw new LispException(""); // TODO

                    if (!(binding.car instanceof Symbol))
                        throw new TypeException(binding.car, Symbol.class);

                    symbols.add((Symbol) binding.car);

                    // get and evaluate the value
                    binding = binding.cdr;
                    if (binding == null) values.add(SExp.NIL);
                    else values.add(binding.car.eval(symbolTable));

                    // next let binding
                    letBinding = letBinding.cdr;
                }

                // perform actual binding in a new scope
                newScope = new SymbolTable(symbolTable);

                for (int i = 0; i < symbols.size(); ++i)
                    newScope.bind(symbols.get(i),
                        new VariableEntry(values.get(i)));

                // evaluate all forms with the new scope
                arguments = arguments.cdr;

                while (arguments != null) {
                    retVal = arguments.car.eval(newScope);
                    arguments = arguments.cdr;
                }

                return retVal;
            }
        };

        // ----
        // LET*
        // ----

        final SpecialFormEntry LET_STAR = new SpecialFormEntry(
            new Symbol("LET*"),
            environment,
            new FormHelpTopic("LET*", "create new lexical variable bindings",
                "(let (([<var> <init-form>])*) <form>*) => <result>",
                "let* creates new variable bindings and executes a series of "
                    + "forms that use these bindings. let* performs the "
                    + "bindings sequentially (as opposed to let which does "
                    + "them in parallel). The expression for the init-form of "
                    + "a var can refer to vars previously bound in the let*."
                    + "The form (let* ((var1 init-form-1) (var2 init-form-2)"
                    + " ... (varm init-form-m)) form1 form2 ... formn) first "
                    + "evaluates the expression init-form-1, then binds the "
                    + "variable var1 to that value; then it evaluates "
                    + "init-form-2 and binds var2, and so on. The expressions "
                    + "formj are then evaluated in order; the values of all "
                    + "but the last are discarded (that is, the body of let* "
                    + "is an implicit progn).",
                "var", "a symbol",
                "init-form", "a form",
                "form", "a form",
                "result", "the value returned by the last form"),
            -1, List.class, SExp.class)
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                SymbolTable newScope;
                Seq letBinding;
                Symbol var;
                SExp initvalue;
                SExp retVal = SExp.NIL;

                checkArguments(arguments);

                letBinding = ((List) arguments.car).seq;

                // perform actual binding in a new scope
                newScope = new SymbolTable(symbolTable);

                while (letBinding != null) {

                    // each binding should be a list of a symbol and form
                    if (!(letBinding.car instanceof List))
                        throw new LispException("Malformed LET bindings: "
                            + letBinding.car.toString());

                    // get the symbol
                    Seq binding = ((List) letBinding.car).seq;
                    if (binding == null) 
                        throw new LispException(""); // TODO

                    if (!(binding.car instanceof Symbol))
                        throw new TypeException(binding.car, Symbol.class);

                    var = (Symbol) binding.car;

                    // get and evaluate the value
                    // include already bound variables from the let* in the
                    // scope for this evaluation
                    binding = binding.cdr;
                    if (binding == null) initvalue = SExp.NIL;
                    else initvalue = binding.car.eval(newScope);

                    newScope.bind(var, new VariableEntry(initvalue));

                    // next let binding
                    letBinding = letBinding.cdr;
                }

                // evaluate all forms with the new scope
                arguments = arguments.cdr;

                while (arguments != null) {
                    retVal = arguments.car.eval(newScope);
                    arguments = arguments.cdr;
                }

                return retVal;
            }
        };

        // ----
        // LIST
        // ----

        final SpecialFormEntry LIST = new SpecialFormEntry(
            new Symbol("LIST"),
            environment,
            new FormHelpTopic("LIST", "create a list",
                "(list <object>*) => list",
                "list returns a list containing the supplied objects.",
                "object", "an object.",
                "list", "a list."),
            -1, SExp.class) // actually accepts 0 or more args, not 1 or more
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                ArrayList<SExp> values = new ArrayList<SExp>();
                Seq retVal = null;

                if (arguments == null) return SExp.NIL;

                while(arguments != null) {
                    // eval and push the current object
                    values.add(arguments.car.eval(symbolTable));

                    // next object in argument list
                    arguments = arguments.cdr;
                }

                for (int i = values.size(); i != 0;)
                    retVal = new Seq(values.get(--i), retVal);

                return new List(retVal);
            }
        };

        // -----
        // QUOTE
        // -----

        final SpecialFormEntry QUOTE = new SpecialFormEntry(
            new Symbol("QUOTE"),
            environment,
            new FormHelpTopic("QUOTE", "Return objects unevaluated.",
                "(quote <object>) => <object>",
                "The quote special operator just returns object. The "
                + "consequences are undefined if literal objects (including "
                + "quoted objects) are destructively modified. ",
                "object", "an object; not evaluated."),
            1, SExp.class)
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                if (arguments == null)
                    throw new InvalidArgumentQuantityException(1, 0);

                return arguments.car;
            }
        };

        // -----
        // PROGN
        // -----

        final SpecialFormEntry PROGN = new SpecialFormEntry(
            new Symbol("PROGN"),
            environment,
            new FormHelpTopic("PROGN",
                "evaluate forms in the order they are given",
                "(progn <form>*) => <result>*",
                "progn evaluates forms, in the order in which they are given. "
                    + "The values of each form but the last are discarded. If "
                    + "progn appears as a top level form, then all forms "
                    + "within that progn are considered by the compiler to be "
                    + "top level forms. ",
                "form", "a list of forms",
                "result", "the value of the last form"),
            -1, SExp.class) // actually accepts 0 or more args
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                SExp result = SExp.NIL;

                // evaluate all forms, left to right
                while (arguments != null)  {
                    result = arguments.car.eval(symbolTable);
                    arguments = arguments.cdr;
                }

                return result;
            }
        };

        // ----
        // SETQ
        // ----

        final SpecialFormEntry SETQ = new SpecialFormEntry(
            new Symbol("SETQ"),
            environment,
            new FormHelpTopic("SETQ", "Assigns values to variables.",
                "(setq [<name> <form>]*)",
                "Assigns values to variables.  (setq var1 form1 var2 "
                    + "form2 ...) is the simple variable assignment statement "
                    + "of Lisp. First form1 is evaluated and the result is "
                    + "stored in the variable var1, then form2 is evaluated "
                    + "and the result stored in var2, and so forth. setq may "
                    + "be used for assignment of both lexical and dynamic "
                    + "variables. If any var refers to a binding made by "
                    + "symbol-macrolet, then that var is treated as if setf "
                    + "(not setq) had been used. ",
                "name",
                "a symbol naming a variable other than a constant variable",
                "form", "a form"),
            -1, SExp.class) //actually accepts an even number of args
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                
                Symbol variableName;
                SExp variableValue = SExp.NIL;

                if (arguments.length() % 2 != 0)
                    throw new InvalidArgumentQuantityException(
                        "there must be an even number of arguments "
                        + "(name, value pairs)");

                // treat each pair
                while (arguments != null) {

                    // first argument of pair: Symbol for variable name
                    if (!(arguments.car instanceof Symbol))
                        throw new TypeException(arguments.car, Symbol.class);

                    variableName = (Symbol) arguments.car;

                    // TODO: check for redifinition of variable and warn or err
                    // if the variable is a constant

                    // second argument: variable value
                    arguments = arguments.cdr;
                    assert (arguments != null);

                    variableValue = arguments.car.eval(symbolTable);

                    symbolTable.rebind(variableName,
                        new VariableEntry(variableValue));

                    arguments = arguments.cdr;
                }

                return variableValue;
            }
        };

        // -----
        // TRACE
        // -----

        final SpecialFormEntry TRACE = new SpecialFormEntry(
            new Symbol("TRACE"),
            environment,
            new FormHelpTopic("TRACE",
                "enable trace information for a function",
                "(trace <funcname>)",
                "Turn on trace information for a function.",
                "funcname", "the name of the function to trace"),
            -1, Symbol.class) //actually accepts 0 or 1 arg
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                if (arguments == null || arguments.car == null)
                    return SExp.NIL;

                if (!(arguments.car instanceof Symbol))
                    throw new LispException(arguments.car.toString()
                        + " is not a valid function name.");

                FormEntry fe = symbolTable.lookupFunction((Symbol) arguments.car);

                if (fe == null)
                    throw new UndefinedFunctionException(
                        ((Symbol) arguments.car));

                if (fe instanceof FunctionEntry)
                    ((FunctionEntry) fe).enableTrace(true);

                // TODO: else throw error

                return SExp.NIL;
            }
        };

        // ----
        // QUIT
        // ----

        final SpecialFormEntry QUIT = new SpecialFormEntry(
            new Symbol("QUIT"),
            environment,
            new FormHelpTopic("QUIT", "Exit the interpreter.",
                "(quit)",
                ""),
            0, SExp.class)
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                checkArguments(arguments);
                environment.signalStop();
                return SExp.NIL;
            }
        };

        environment.globalSymbolTable.bind(LTE.name, LTE);
        environment.globalSymbolTable.bind(LT.name, LT);
        environment.globalSymbolTable.bind(NUMEQ.name, NUMEQ);
        environment.globalSymbolTable.bind(NUMNOTEQ.name, NUMNOTEQ);
        environment.globalSymbolTable.bind(GT.name, GT);
        environment.globalSymbolTable.bind(GTE.name, GTE);
        environment.globalSymbolTable.bind(DIF.name, DIF);
        environment.globalSymbolTable.bind(DIV.name, DIV);
        environment.globalSymbolTable.bind(MUL.name, MUL);
        environment.globalSymbolTable.bind(SUM.name, SUM);
        environment.globalSymbolTable.bind(CONS.name, CONS);
        environment.globalSymbolTable.bind(DEFUN.name, DEFUN);
        environment.globalSymbolTable.bind(DEFPARAMETER.name, DEFPARAMETER);
        environment.globalSymbolTable.bind(DEFVAR.name, DEFVAR);
        environment.globalSymbolTable.bind(ENABLEDEBUGAST.name, ENABLEDEBUGAST);
        environment.globalSymbolTable.bind(FUNCALL.name, FUNCALL);
        environment.globalSymbolTable.bind(FUNCTION.name, FUNCTION);
        environment.globalSymbolTable.bind(GETF.name, GETF);
        environment.globalSymbolTable.bind(HELP.name, HELP);
        environment.globalSymbolTable.bind(IF.name, IF);
        environment.globalSymbolTable.bind(LAMBDA.name, LAMBDA);
        environment.globalSymbolTable.bind(LET.name, LET);
        environment.globalSymbolTable.bind(LET_STAR.name, LET_STAR);
        environment.globalSymbolTable.bind(LIST.name, LIST);
        environment.globalSymbolTable.bind(QUOTE.name, QUOTE);
        environment.globalSymbolTable.bind(PROGN.name, PROGN);
        environment.globalSymbolTable.bind(SETQ.name, SETQ);
        environment.globalSymbolTable.bind(TRACE.name, TRACE);
        environment.globalSymbolTable.bind(QUIT.name, QUIT);
    }
}

package edu.utexas.cs345.jdblisp;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * SpecialFormEntry
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public abstract class SpecialFormEntry extends FormEntry {

    protected LISPRuntime environment;

    public SpecialFormEntry(Symbol name, LISPRuntime environment,
    HelpTopic helpinfo) {
        super(name, helpinfo);

        this.environment = environment;
    }

    public abstract SExp call(SymbolTable symbolTable, Seq arguments)
        throws LispException;

    @Override
    public String display(String offset) {
        return offset + "Special Form Entry: " + symbol.toString();
    }

    @Override
    public String toString() {
        return "<SPECIAL-FORM (" + symbol.toString() + ") >";
    }

    static final Symbol LTE                 = new Symbol("<=");
    static final Symbol LT                  = new Symbol("<");
    static final Symbol NUMEQ               = new Symbol("=");
    static final Symbol NUMNOTEQ            = new Symbol("/=");
    static final Symbol GT                  = new Symbol(">");
    static final Symbol GTE                 = new Symbol(">=");
    static final Symbol DIV                 = new Symbol("/");
    static final Symbol DIF                 = new Symbol("-");
    static final Symbol MUL                 = new Symbol("*");
    static final Symbol SUM                 = new Symbol("+");
    static final Symbol CAR                 = new Symbol("CAR");
    static final Symbol CDR                 = new Symbol("CDR");
    static final Symbol CONS                = new Symbol("CONS");
    static final Symbol DEFUN               = new Symbol("DEFUN");
    static final Symbol DEFPARAMETER        = new Symbol("DEFPARAMETER");
    static final Symbol DEFVAR              = new Symbol("DEFVAR");
    static final Symbol ENABLEDEBUGAST      = new Symbol("ENABLE-DEBUG-AST");
    static final Symbol FUNCTION            = new Symbol("FUNCTION");
    static final Symbol FUNCALL             = new Symbol("FUNCALL");
    static final Symbol GETF                = new Symbol("GETF");
    static final Symbol HELP                = new Symbol("HELP");
    static final Symbol IF                  = new Symbol("IF");
    static final Symbol LABELS              = new Symbol("LABELS");
    static final Symbol LAMBDA              = new Symbol("LAMBDA");
    static final Symbol LET                 = new Symbol("LET");
    static final Symbol LET_STAR            = new Symbol("LET*");
    static final Symbol LETREC              = new Symbol("LETREC");
    static final Symbol LIST                = new Symbol("LIST");
    static final Symbol MOD                 = new Symbol("MOD");
    static final Symbol NOT                 = new Symbol("NOT");
    static final Symbol NULL                = new Symbol("NULL?");
    static final Symbol QUOTE               = new Symbol("QUOTE");
    static final Symbol PROGN               = new Symbol("PROGN");
    static final Symbol REM                 = new Symbol("REM");
    static final Symbol SETQ                = new Symbol("SETQ");
    static final Symbol TRACE               = new Symbol("TRACE");
    static final Symbol QUIT                = new Symbol("QUIT");

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
            SpecialFormEntry.LTE, environment,
            new FormHelpTopic("<=", "Less than or equal to",
                "(<= <number>*) => <result>",
                "The value of <= is true if the numbers are in monotonically "
                    + "nondecreasing order; otherwise it is false.",
                "number", "a real",
                "result", "a boolean"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num current;
                Num next;

                // check that there is at least one argument
                if (arguments == null)
                    throw new InvalidArgumentQuantityException(toString(),
                        "at least one argument required.");

                // get first number
                current = TypeUtil.attemptCast(Num.class,
                    arguments.car.eval(symbolTable));

                // advance to next argument
                arguments = arguments.cdr;

                while(arguments != null) {
                    // get next number
                    next = TypeUtil.attemptCast(Num.class,
                        arguments.car.eval(symbolTable));

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
            SpecialFormEntry.LT, environment,
            new FormHelpTopic("<", "Less than",
                "(< <number>*) => <result>",
                "The value of < is true if the numbers are in monotonically "
                    + "increasing order; otherwise it is false.",
                "number", "a real",
                "result", "a boolean"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num current;
                Num next;

                if (arguments == null)
                    throw new InvalidArgumentQuantityException(toString(),
                        "at least one argument is required.");

                // get first number
                current = TypeUtil.attemptCast(Num.class,
                    arguments.car.eval(symbolTable));

                // advance to next argument
                arguments = arguments.cdr;

                while(arguments != null) {
                    // get next number
                    next = TypeUtil.attemptCast(Num.class,
                        arguments.car.eval(symbolTable));

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
            SpecialFormEntry.NUMEQ, environment,
            new FormHelpTopic("=", "Equal to",
                "(= <number>*) => <result>",
                "The value of = is true if all numbers are the same in value.",
                "number", "a number",
                "result", "a boolean"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num current;
                Num next;

                if (arguments == null)
                    throw new InvalidArgumentQuantityException(toString(),
                        "at least one argument is required.");

                // get first number
                current = TypeUtil.attemptCast(Num.class,
                    arguments.car.eval(symbolTable));

                // advance to next argument
                arguments = arguments.cdr;

                while(arguments != null) {
                    // get next number
                    next = TypeUtil.attemptCast(Num.class,
                        arguments.car.eval(symbolTable));

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
            SpecialFormEntry.NUMNOTEQ, environment,
            new FormHelpTopic("/=", "Not equal to",
                "(/= <number>*) => <result>",
                "The value of /= is true if no two numbers are the same in value.",
                "number", "a number",
                "result", "a boolean"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num current;
                Num next;

                if (arguments == null)
                    throw new InvalidArgumentQuantityException(toString(),
                        "at least one argument is required.");

                // get first number
                current = TypeUtil.attemptCast(Num.class,
                    arguments.car.eval(symbolTable));

                // advance to next argument
                arguments = arguments.cdr;

                while(arguments != null) {
                    // get next number
                    next = TypeUtil.attemptCast(Num.class,
                        arguments.car.eval(symbolTable));

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
            SpecialFormEntry.GT, environment,
            new FormHelpTopic(">", "Greater than",
                "(> <number>*) => <result>",
                "The value of > is true if the numbers are in monotonically "
                    + "decreasing order; otherwise it is false.",
                "number", "a number",
                "result", "a boolean"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num current;
                Num next;

                if (arguments == null)
                    throw new InvalidArgumentQuantityException(toString(),
                        "at least one argument is required.");

                // get first number
                current = TypeUtil.attemptCast(Num.class,
                    arguments.car.eval(symbolTable));

                // advance to next argument
                arguments = arguments.cdr;

                while(arguments != null) {
                    // get next number
                    next = TypeUtil.attemptCast(Num.class,
                        arguments.car.eval(symbolTable));

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
            SpecialFormEntry.GTE, environment,
            new FormHelpTopic(">=", "Greater than or equal to",
                "(>= <number>*) => <result>",
                "The value of > is true if the numbers are in monotonically "
                    + "non-increasing order; otherwise it is false.",
                "number", "a number",
                "result", "a boolean"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num current;
                Num next;

                if (arguments == null)
                    throw new InvalidArgumentQuantityException(toString(),
                        "at least one argument is required.");

                // get first number
                current = TypeUtil.attemptCast(Num.class,
                    arguments.car.eval(symbolTable));

                // advance to next argument
                arguments = arguments.cdr;

                while(arguments != null) {
                    // get next number
                    next = TypeUtil.attemptCast(Num.class,
                        arguments.car.eval(symbolTable));

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
            SpecialFormEntry.DIV, environment,
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
                    + "to a number."))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num dividend = new Num("1");
                Num firstArg;

                if (arguments == null)
                    throw new InvalidArgumentQuantityException(toString(),
                        "at least one argument is required.");

                // case: only one argument: 1 / arg
                firstArg = TypeUtil.attemptCast(Num.class,
                    arguments.car.eval(symbolTable));

                dividend = dividend.divideBy(firstArg);

                arguments = arguments.cdr;
                if (arguments == null) return dividend;

                // case: (/ x y1 ... yn)
                dividend = firstArg;

                // variable number of arguments [0..inf)
                while (arguments != null) {
                    dividend = dividend.divideBy(TypeUtil.attemptCast(
                        Num.class,
                        arguments.car.eval(symbolTable)));
                    arguments = arguments.cdr;
                }

                return dividend;
            }
        };

        // ---
        // DIF
        // ---

        final SpecialFormEntry DIF = new SpecialFormEntry(
            SpecialFormEntry.DIF, environment,
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
                    + "that evaluates to a number."))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num difference = new Num("0");

                if (arguments == null)
                    throw new InvalidArgumentQuantityException(toString(),
                        "at least one argument is required.");

                // case: only one argument: 0 - arg
                difference = difference.subtract(TypeUtil.attemptCast(
                    Num.class,
                    arguments.car.eval(symbolTable)));

                arguments = arguments.cdr;
                if (arguments == null) return difference;

                // case: (- x y1 ... yn)
                difference = difference.negate();

                // variable number of arguments [0..inf)
                while (arguments != null)  {
                    difference = difference.subtract(TypeUtil.attemptCast(
                        Num.class,
                        arguments.car.eval(symbolTable)));
                    arguments = arguments.cdr;
                }

                return difference;
            }
        };

        // ---
        // MUL
        // ---

        final SpecialFormEntry MUL = new SpecialFormEntry(
            SpecialFormEntry.MUL, environment,
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
                    product = product.multiply(TypeUtil.attemptCast(
                        Num.class, arguments.car.eval(symbolTable)));
                    arguments = arguments.cdr;
                }

                return product;
            }
        };

        // ---
        // SUM
        // ---

        final SpecialFormEntry SUM = new SpecialFormEntry(
            SpecialFormEntry.SUM, environment,
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
                    sum = sum.add(TypeUtil.attemptCast(
                        Num.class,arguments.car.eval(symbolTable)));
                    arguments = arguments.cdr;
                }

                return sum;
            }
        };

        // ---
        // CAR
        // ---

        final SpecialFormEntry CAR = new SpecialFormEntry(
            SpecialFormEntry.CAR, environment,
            new FormHelpTopic("CAR", "get first element of a list",
                "(car <List>) => <SExp>",
                "Return the first element of a List or Cons",
                "List", "a list",
                "car", "an sexp"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
     
                if (arguments == null)
                    throw new InvalidArgumentQuantityException(toString(), 1);

                SExp evaluatedArg = arguments.car.eval(symbolTable);

                // check for NIL case
                if (SExp.NIL.equals(evaluatedArg)) return SExp.NIL;

                // TypeUtil.attemptCast is not quite good enough here, we need
                // to check against two possible classes.
                if (evaluatedArg instanceof List) 
                    return ((List) evaluatedArg).seq.car;

                // rare use case, but possible and the CL spec says it should still accept
                else if (evaluatedArg instanceof Seq) 
                    return ((Seq) evaluatedArg).car;
                else throw new TypeException(arguments.car, List.class);
            }
        };   

        // ---
        // CDR
        // ---

        final SpecialFormEntry CDR = new SpecialFormEntry(
            SpecialFormEntry.CDR, environment,
            new FormHelpTopic("CDR", "get the cdr of a list or cons",
                "(cdr <List>) => <SExp>",
                "Returns the cdr of a List or Cons",
                "List", "a list",
                "cdr", "an sexp"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
     
                if (arguments == null)
                    throw new InvalidArgumentQuantityException(toString(), 1);

                SExp evaluatedArg = arguments.car.eval(symbolTable);

                // check for NIL case
                if (SExp.NIL.equals(evaluatedArg)) return SExp.NIL;

                // TypeUtil.attemptCast is not quite good enough here, we need
                // to check against two possible classes.
                if (evaluatedArg instanceof List) 
                    return new List(((List) evaluatedArg).seq.cdr);

                // rare use case, but possible and the CL spec says it should still accept
                else if (evaluatedArg instanceof Seq)
                    return ((Seq) evaluatedArg).cdr;
                else throw new TypeException(arguments.car, List.class);
            }
        };   

        // ----
        // CONS
        // ----

        final SpecialFormEntry CONS = new SpecialFormEntry(
            SpecialFormEntry.CONS, environment,
            new FormHelpTopic("CONS", "create a cons",
                "(cons <object-1> <object-2>) => <cons>",
                "Creates a fresh cons, the car of which is object-1 and the "
                    + "cdr of which is object-2.",
                "object-1", "an object",
                "object-2", "an object",
                "cons", "a cons"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                
                SExp object1, object2;
                Cons cons;

                if (arguments == null || arguments.cdr == null)
                    throw new InvalidArgumentQuantityException(toString(), 2);

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
            SpecialFormEntry.DEFUN, environment,
            new FormHelpTopic("DEFUN", "Define a (global) function.",
                "(defun <name> (<param-list>) <func-body>)",
                "Create a function binding. This will replace any existing binding.",
                "name", "the symbol to bind to the function definition, "
                    + "not evaluated.",
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

                // check for the correct number of arguments
                if (arguments == null)
                    throw new InvalidArgumentQuantityException(
                        toString(), 3, 0);
                if (arguments.length() != 3)
                    throw new InvalidArgumentQuantityException(
                        toString(), 3, arguments.length());

                // TODO: check to see if a function for this symbol exists
                // and warn if so

                // first argument: Symbol for function name
                functionName = TypeUtil.attemptCast(
                    Symbol.class, arguments.car);

                // second argument, parameter list
                arguments = arguments.cdr;
                assert (arguments != null);
                
                // read parameters
                Seq paramSeq = TypeUtil.attemptCast(
                    List.class, arguments.car).seq;

                while (paramSeq != null) {
                    parameters.add(TypeUtil.attemptCast(
                        Symbol.class, paramSeq.car));
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

        final SpecialFormEntry DEFPARAM = new SpecialFormEntry(
            SpecialFormEntry.DEFPARAMETER, environment,
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
                "documentation", "a string; not evaluated."))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Symbol name;
                SExp initValue = null;
                HelpTopic helpinfo = null;

                if (arguments == null || arguments.length() < 1)
                    throw new InvalidArgumentQuantityException(
                        toString(), 1, 0);

                // first argument: variable name
                name = TypeUtil.attemptCast(Symbol.class, arguments.car);

                // second argument: initial value
                arguments = arguments.cdr;
                if (arguments != null) {
                    initValue = arguments.car.eval(symbolTable);
                
                    // third argument: documentation
                    arguments = arguments.cdr;
                    if (arguments != null) 
                        helpinfo = new HelpTopic(name.toString(), "variable", 
                            TypeUtil.attemptCast(
                                Str.class, arguments.car).value);
                }

                environment.globalSymbolTable.bind(name,
                    new VariableEntry(name, initValue, false, helpinfo));

                return name;

            }
        };

        // ------
        // DEFVAR
        // ------

        final SpecialFormEntry DEFVAR = new SpecialFormEntry(
            SpecialFormEntry.DEFVAR, environment,
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
                "documentation", "a string; not evaluated."))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                
                Symbol name;
                SExp initValue = null;
                HelpTopic helpinfo = null;

                if (arguments == null || arguments.length() < 1)
                    throw new InvalidArgumentQuantityException(
                        toString(), 1, 0);

                // first argument: variable name
                name = TypeUtil.attemptCast(Symbol.class, arguments.car);

                // if this variable is already defined, return without
                // setting it
                if (symbolTable.lookupVariable(name) != null)
                    return arguments.car;

                return DEFPARAM.call(symbolTable, arguments);
            }
        };

        // ----------------
        // ENABLE-DEBUG-AST
        // ----------------

        final SpecialFormEntry ENABLEDEBUGAST = new SpecialFormEntry(
            SpecialFormEntry.ENABLEDEBUGAST, environment,
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
            SpecialFormEntry.FUNCTION, environment,
            new FormHelpTopic("FUNCTION", "retrieve a function",
                "(function <func-name>) => <function>",
                "function returns the function specified by the symbol passed "
                    + " as an argument. #'funcname is equivalent to (function "
                    + " funcname).",
                "func-name", "a symbol naming a function",
                "function", "a function"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments) 
            throws LispException {
                FormEntry fe = null;

                if (arguments == null)
                    throw new InvalidArgumentQuantityException(
                        toString(), 1, 0);

                fe = symbolTable.lookupFunction(TypeUtil.attemptCast(
                    Symbol.class, arguments.car));

                if (fe == null)
                    throw new UndefinedFunctionException((Symbol) arguments.car);

                return fe;
            }
        };

        // -------
        // FUNCALL
        // -------

        final SpecialFormEntry FUNCALL = new SpecialFormEntry(
            SpecialFormEntry.FUNCALL, environment,
            new FormHelpTopic("FUNCALL", "make a function call",
                "(funcall <function> <arg>*) => <result>",
                "funcall applies function to args. If function is a symbol, "
                    + "it is coerced to a function as if by finding its "
                    + "functional value in the global environment.",
                "function", "a function designator",
                "arg", "an object",
                "results", "the result of the function call"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                if (arguments == null)
                    throw new InvalidArgumentQuantityException(toString(),
                        "at least one argument is required.");

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
            SpecialFormEntry.GETF, environment,
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
                "value", "an object"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                SExp plistEval;
                Seq plistSeq;
                SExp indicator;
                SExp retVal = SExp.NIL;

                if (arguments == null || arguments.length() < 2)
                    throw new InvalidArgumentQuantityException(
                        toString(), 2, 0);

                // first argument: property list
                plistEval = arguments.car.eval(symbolTable);
                plistSeq = TypeUtil.attemptCast(List.class, plistEval).seq;

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
            SpecialFormEntry.HELP, environment,
            new FormHelpTopic("HELP",
                "Display online help information for a topic.",
                "(help [<topic>*])",
                null,
                "topic",
                "either a string representing the topic to lookup or a symbol"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                
                ArrayList<HelpTopic> topics = new ArrayList<HelpTopic>();

                // no arguments: print help for HELP
                if (arguments == null)
                    return this.call(symbolTable,
                        new Seq(SpecialFormEntry.HELP, null));

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
            SpecialFormEntry.IF, environment,
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
                    + "returned by the else-form."))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments) 
            throws LispException {

                if (arguments == null || arguments.length() < 2)
                    throw new InvalidArgumentQuantityException(toString(), 2);

                // evaluate test form
                SExp testResult = arguments.car.eval(symbolTable);

                // advance to then-form
                arguments = arguments.cdr;

                // if false, advance to else-form
                if (testResult == null || testResult == SExp.NIL)
                    arguments = arguments.cdr;

                if (arguments == null) return SExp.NIL;

                return arguments.car.eval(symbolTable);
            }
        };

        // ------
        // LABELS
        // ------

        final SpecialFormEntry LABELS = new SpecialFormEntry(
            SpecialFormEntry.LABELS, environment,
            new FormHelpTopic("LABELS", "recursive function definition",
                "(labels ((function-name (param*) local-form*)*) form*) => "
                    + "result",
                "labels defines locally named functions and executes a series "
                    + "of forms with these definition bindings. Any number of "
                    + "such local functions can be defined. The scope of the "
                    + "defined function names for labels encompasses the "
                    + "function definitions themselves as well as the body.",
                "function-name", "a symbol",
                "param", "a symbol",
                "local-form", "a form (the list of local-forms is an implicit "
                    + "progn.",
                "form", "a form (the list of forms is an implicit progn."))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                ArrayList<FormEntry> localFunctions = new ArrayList<FormEntry>();
                ArrayList<Symbol> params;
                LinkedList<SExp> localForms;
                Seq labelsSeq, defunSeq, paramSeq;
                SExp funcBody, result = SExp.NIL;
                Symbol funcName;
                SymbolTable newScope;

                if (arguments == null)
                    throw new InvalidArgumentQuantityException(toString(),
                        "at least one argument is required.");

                labelsSeq = TypeUtil.attemptCast(List.class, arguments.car).seq;

                // parse each local function definition
                while (labelsSeq != null) {
                    defunSeq = TypeUtil.attemptCast(List.class,
                        labelsSeq.car).seq;

                    if (defunSeq == null || defunSeq.length() < 3)
                        throw new LispException("Malformed LABELS expression: "
                            + "function definition list is incomplete.");

                    funcName = TypeUtil.attemptCast(Symbol.class, defunSeq.car);
                    defunSeq = defunSeq.cdr;
                    paramSeq = TypeUtil.attemptCast(List.class,
                        defunSeq.car).seq;
                    defunSeq = defunSeq.cdr;

                    // capture each parameter to this function
                    params = new ArrayList<Symbol>();
                    while (paramSeq != null) {
                        params.add(TypeUtil.attemptCast(Symbol.class,
                            paramSeq.car));
                        paramSeq = paramSeq.cdr;
                    }

                    // capture each local form
                    localForms = new LinkedList<SExp>();
                    while(defunSeq != null) {
                        localForms.add(defunSeq.car);
                        defunSeq = defunSeq.cdr;
                    }

                    // create the implicit PROGN
                    localForms.addFirst(SpecialFormEntry.PROGN);
                    funcBody = new List(new Seq(
                        localForms.toArray(new SExp[]{})));

                    // create the FunctionEntry
                    localFunctions.add(new FunctionEntry(funcName,
                        params.toArray(new Symbol[]{}), funcBody));

                    // next function definition
                    labelsSeq = labelsSeq.cdr;
                }

                // create the new scope to include the new function definitions
                newScope = new SymbolTable(symbolTable);
                for (FormEntry fe : localFunctions)
                    newScope.bind(fe.symbol, fe);

                // advance to the body of the LABELS form, the implicit PROGN
                // of forms
                arguments = arguments.cdr;
                while (arguments != null) {
                    result = arguments.car.eval(newScope);
                    arguments = arguments.cdr;
                }

                return result;
            }
        };

        // ------
        // LAMBDA
        // ------

        final SpecialFormEntry LAMBDA = new SpecialFormEntry(
            SpecialFormEntry.LAMBDA, environment,
            new FormHelpTopic("LAMDA",
                "create an anonymous function over the current lexical closure",
                "(lambda <param-list> <form>) => <lambda>",
                "",
                "param-list", "a list of symbols",
                "form", "a form",
                "lambda", "a function"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                
                ArrayList<Symbol> parameters = new ArrayList<Symbol>();
                SExp body;
                Seq paramSeq;

                if (arguments == null || arguments.length() != 2)
                    throw new InvalidArgumentQuantityException(toString(), 2);

                // first parameter: parameters to the lambda
                paramSeq = TypeUtil.attemptCast(List.class, arguments.car).seq;
                while (paramSeq != null) {
                    parameters.add(TypeUtil.attemptCast(
                        Symbol.class, paramSeq.car));
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
            SpecialFormEntry.LET, environment,
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
                "result", "the value returned by the last form"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                
                SymbolTable newScope;
                Seq letBinding;
                ArrayList<Symbol> symbols = new ArrayList<Symbol>();
                ArrayList<SExp> values = new ArrayList<SExp>();
                SExp retVal = SExp.NIL;

                if (arguments == null || arguments.length() < 1)
                    throw new InvalidArgumentQuantityException(toString(), 1);

                letBinding = TypeUtil.attemptCast(
                    List.class, arguments.car).seq;

                while (letBinding != null) {

                    // each binding should be a list of a symbol and form
                    if (!(letBinding.car instanceof List))
                        throw new LispException("Malformed LET bindings: "
                            + letBinding.car.toString());

                    // get the symbol
                    Seq binding = TypeUtil.attemptCast(
                        List.class, letBinding.car).seq;

                    if (binding == null) 
                        throw new LispException(""); // TODO

                    symbols.add(TypeUtil.attemptCast(Symbol.class, binding.car));

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
                        new VariableEntry(symbols.get(i), values.get(i)));

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
            SpecialFormEntry.LET_STAR, environment,
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
                "result", "the value returned by the last form"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                SymbolTable newScope;
                Seq letBinding;
                Symbol var;
                SExp initvalue;
                SExp retVal = SExp.NIL;

                if (arguments == null || arguments.length() < 1)
                    throw new InvalidArgumentQuantityException(toString(), 1);

                letBinding = TypeUtil.attemptCast(
                    List.class, arguments.car).seq;

                // perform actual binding in a new scope
                newScope = new SymbolTable(symbolTable);

                while (letBinding != null) {

                    // each binding should be a list of a symbol and form
                    if (!(letBinding.car instanceof List))
                        throw new LispException("Malformed LET bindings: "
                            + letBinding.car.toString());

                    // get the symbol
                    Seq binding = TypeUtil.attemptCast(
                        List.class, letBinding.car).seq;

                    if (binding == null) 
                        throw new LispException(""); // TODO

                    var = TypeUtil.attemptCast(Symbol.class, binding.car);

                    // get and evaluate the value
                    // include already bound variables from the let* in the
                    // scope for this evaluation
                    binding = binding.cdr;
                    if (binding == null) initvalue = SExp.NIL;
                    else initvalue = binding.car.eval(newScope);

                    newScope.bind(var, new VariableEntry(var, initvalue));

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

        // ------
        // LETREC
        // ------

        // This is a Scheme function. It exists in CLISP as LABELS. The 
        // special form in JLisp is provided as an alias for LABELS

        // ----
        // LIST
        // ----

        final SpecialFormEntry LIST = new SpecialFormEntry(
            SpecialFormEntry.LIST, environment,
            new FormHelpTopic("LIST", "create a list",
                "(list <object>*) => list",
                "list returns a list containing the supplied objects.",
                "object", "an object.",
                "list", "a list."))
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

        // ---
        // MOD
        // ---

        // TODO: this does not follow the Common Lisp standard (which requires
        // FLOOR, TRUNCATE, and others to be defined). Fix in future when the
        // required functions are defined.
        final SpecialFormEntry MOD = new SpecialFormEntry(
            SpecialFormEntry.MOD, environment,
            new FormHelpTopic("MOD", "modulus",
                "(mod <number> <divisor>) => <result>",
                "mod performs the operation floor on number and divisor and "
                    + "returns the remainder of the floor operation. mod is "
                    + "the modulus function when number and divisor are "
                    + "integers. ",
                "number", "a real.",
                "divisor", "a real.",
                "result", "a real."))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                
                Num dividend, divisor;

                if (arguments == null || arguments.length() != 2)
                    throw new InvalidArgumentQuantityException(toString(), 2);

                dividend = TypeUtil.attemptCast(Num.class,
                    arguments.car.eval(symbolTable));
                divisor = TypeUtil.attemptCast(Num.class,
                    arguments.cdr.car);

                return dividend.remainder(divisor);
            }
        };

        // ---
        // NOT (also mapped to NULL?)
        // ---

        final SpecialFormEntry NOT = new SpecialFormEntry(
            SpecialFormEntry.NOT, environment,
            new FormHelpTopic("NOT", "Returns t if x is false; otherwise, "
                + "returns nil.",
                "(not <object>) => <object>",
                "The not operator returns T iff the object passed as a "
                + " parameter is equal to NIL and NIL otherwise.",
                "object", "a generalized boolean (any object)"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments) 
            throws LispException {
                if (arguments == null || arguments.length() != 1)
                    throw new InvalidArgumentQuantityException(toString(), 1);

                if (SExp.NIL.equals(arguments.car.eval(symbolTable)))
                    return SExp.T;
                else return SExp.NIL;
            }
        };
        // -----
        // QUOTE
        // -----

        final SpecialFormEntry QUOTE = new SpecialFormEntry(
            SpecialFormEntry.QUOTE, environment,
            new FormHelpTopic("QUOTE", "Return objects unevaluated.",
                "(quote <object>) => <object>",
                "The quote special operator just returns object. The "
                + "consequences are undefined if literal objects (including "
                + "quoted objects) are destructively modified. ",
                "object", "an object; not evaluated."))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                if (arguments == null)
                    throw new InvalidArgumentQuantityException(
                        toString(), 1, 0);

                return arguments.car;
            }
        };

        // -----
        // PROGN
        // -----

        final SpecialFormEntry PROGN = new SpecialFormEntry(
            SpecialFormEntry.PROGN, environment,
            new FormHelpTopic("PROGN",
                "evaluate forms in the order they are given",
                "(progn <form>*) => <result>*",
                "progn evaluates forms, in the order in which they are given. "
                    + "The values of each form but the last are discarded. If "
                    + "progn appears as a top level form, then all forms "
                    + "within that progn are considered by the compiler to be "
                    + "top level forms. ",
                "form", "a list of forms",
                "result", "the value of the last form"))
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
            SpecialFormEntry.SETQ, environment,
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
                "form", "a form"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                
                Symbol variableName;
                SExp variableValue = SExp.NIL;

                if (arguments == null || arguments.length() % 2 != 0)
                    throw new InvalidArgumentQuantityException(toString(),
                        "there must be a positive, even number of arguments "
                        + "(name, value pairs)");

                // treat each pair
                while (arguments != null) {

                    // first argument of pair: Symbol for variable name
                    variableName = TypeUtil.attemptCast(
                        Symbol.class, arguments.car);

                    // TODO: check for redifinition of variable and warn or err
                    // if the variable is a constant

                    // second argument: variable value
                    arguments = arguments.cdr;
                    assert (arguments != null);

                    variableValue = arguments.car.eval(symbolTable);

                    symbolTable.rebind(variableName,
                        new VariableEntry(variableName, variableValue));

                    arguments = arguments.cdr;
                }

                return variableValue;
            }
        };

        // -----
        // TRACE
        // -----

        final SpecialFormEntry TRACE = new SpecialFormEntry(
            SpecialFormEntry.TRACE, environment,
            new FormHelpTopic("TRACE",
                "enable trace information for a function",
                "(trace <funcname>)",
                "Turn on trace information for a function.",
                "funcname", "the name of the function to trace"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                if (arguments == null || arguments.car == null)
                    return SExp.NIL;

                // only parameter: symbol to trace
                Symbol symbol = TypeUtil.attemptCast(
                    Symbol.class, arguments.car);

                FormEntry fe = symbolTable.lookupFunction(symbol);

                if (fe == null)
                    throw new UndefinedFunctionException(symbol);

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
            SpecialFormEntry.QUIT, environment,
            new FormHelpTopic("QUIT", "Exit the interpreter.",
                "(quit)",
                ""))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                environment.signalStop();
                return SExp.NIL;
            }
        };

        environment.globalSymbolTable.bind(LTE.symbol, LTE);
        environment.globalSymbolTable.bind(LT.symbol, LT);
        environment.globalSymbolTable.bind(NUMEQ.symbol, NUMEQ);
        environment.globalSymbolTable.bind(NUMNOTEQ.symbol, NUMNOTEQ);
        environment.globalSymbolTable.bind(GT.symbol, GT);
        environment.globalSymbolTable.bind(GTE.symbol, GTE);
        environment.globalSymbolTable.bind(DIF.symbol, DIF);
        environment.globalSymbolTable.bind(DIV.symbol, DIV);
        environment.globalSymbolTable.bind(MUL.symbol, MUL);
        environment.globalSymbolTable.bind(SUM.symbol, SUM);
        environment.globalSymbolTable.bind(CAR.symbol, CAR);
        environment.globalSymbolTable.bind(CDR.symbol, CDR);
        environment.globalSymbolTable.bind(CONS.symbol, CONS);
        environment.globalSymbolTable.bind(DEFUN.symbol, DEFUN);
        environment.globalSymbolTable.bind(DEFPARAM.symbol, DEFPARAM);
        environment.globalSymbolTable.bind(DEFVAR.symbol, DEFVAR);
        environment.globalSymbolTable.bind(ENABLEDEBUGAST.symbol, ENABLEDEBUGAST);
        environment.globalSymbolTable.bind(FUNCALL.symbol, FUNCALL);
        environment.globalSymbolTable.bind(FUNCTION.symbol, FUNCTION);
        environment.globalSymbolTable.bind(GETF.symbol, GETF);
        environment.globalSymbolTable.bind(HELP.symbol, HELP);
        environment.globalSymbolTable.bind(IF.symbol, IF);
        environment.globalSymbolTable.bind(LABELS.symbol, LABELS);
        environment.globalSymbolTable.bind(LAMBDA.symbol, LAMBDA);
        environment.globalSymbolTable.bind(LET.symbol, LET);
        environment.globalSymbolTable.bind(LET_STAR.symbol, LET_STAR);

        // map LETREC to LABELS (not quite honest. Scheme's LETREC allows you
        // to define variables and functions with LETRECT. In CL, LABELS allows
        // you to recursively define functions only. LET* allows you to
        // recursively define variables).
        environment.globalSymbolTable.bind(SpecialFormEntry.LETREC, LABELS); 
        environment.globalSymbolTable.bind(LIST.symbol, LIST);
        environment.globalSymbolTable.bind(MOD.symbol, MOD);
        environment.globalSymbolTable.bind(NOT.symbol, NOT);
        environment.globalSymbolTable.bind(SpecialFormEntry.NULL, NOT);
        environment.globalSymbolTable.bind(QUOTE.symbol, QUOTE);
        environment.globalSymbolTable.bind(PROGN.symbol, PROGN);
        environment.globalSymbolTable.bind(SETQ.symbol, SETQ);
        environment.globalSymbolTable.bind(TRACE.symbol, TRACE);
        environment.globalSymbolTable.bind(QUIT.symbol, QUIT);
    }
}

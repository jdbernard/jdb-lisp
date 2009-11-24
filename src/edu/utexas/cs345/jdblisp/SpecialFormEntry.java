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

        // ---
        // DIV
        // ---

        final SpecialFormEntry DIV = new SpecialFormEntry(
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
                    + "to a number."))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num dividend = new Num("1");
                Num firstArg;

                if (arguments == null)
                    throw new InvalidArgumentQuantityException(
                        "invalid number of arguments: 0");

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

        // ---
        // DIF
        // ---

        final SpecialFormEntry DIF = new SpecialFormEntry(
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
                    + "that evaluates to a number."))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                Num difference = new Num("0");

                // need at least one argument
                if (arguments == null)
                    throw new InvalidArgumentQuantityException(
                        "invalid number of arguments: 0");

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

        // ---
        // MUL
        // ---

        final SpecialFormEntry MUL = new SpecialFormEntry(
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

        // ---
        // SUM
        // ---

        final SpecialFormEntry SUM = new SpecialFormEntry(
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

        // ----
        // CONS
        // ----

        SpecialFormEntry CONS = new SpecialFormEntry(
            environment,
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

                if (arguments.length() != 2)
                    throw new InvalidArgumentQuantityException(2,
                        arguments.length());

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

        // ------------
        // DEFPARAMETER
        // ------------

        final SpecialFormEntry DEFPARAMETER = new SpecialFormEntry(
            environment,
            new FormHelpTopic("DEFPARAMETER", "define a dynamic variable",
                "(defparameter <name> <initial-value> [<documentation>]) => <name>",
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

                if (arguments == null)
                    throw new InvalidArgumentQuantityException(0);

                // first argument: variable name
                if (!(arguments.car instanceof Symbol))
                    throw new TypeException(arguments.car, Symbol.class);

                name = (Symbol) arguments.car;

                // second argument: initial value
                arguments = arguments.cdr;
                if (arguments != null) {
                    initValue = arguments.car.eval(symbolTable);
                
                    // third argument: documentation
                    arguments = arguments.cdr;
                    if (arguments != null) {
                        if (!(arguments.car instanceof Str))
                            throw new TypeException(arguments.car, Str.class);

                        helpinfo = new HelpTopic(name.toString(), "variable", 
                            ((Str) arguments.car).value);
                    }
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
                "documentation", "a string; not evaluated."))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                
                Symbol name;
                SExp initValue = null;
                HelpTopic helpinfo = null;

                if (arguments == null)
                    throw new InvalidArgumentQuantityException(0);

                // first argument: variable name
                if (!(arguments.car instanceof Symbol))
                    throw new TypeException(arguments.car, Symbol.class);

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
                    return SExp.NIL;
                }

                SExp retVal = arguments.car.eval(symbolTable);

                if (retVal != null) environment.dumpAST = true;
                else environment.dumpAST = false;

                return retVal;
            }
        };

        // ----
        // GETF
        // ----

        SpecialFormEntry GETF = new SpecialFormEntry(
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
                "value", "an object"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                SExp plistEval;
                Seq plistSeq;
                SExp indicator;
                SExp retVal = SExp.NIL;

                // check number of arguments
                if (arguments.length() < 2)
                    throw new InvalidArgumentQuantityException(
                        "form requires at least 2 arguments.");

                // first argument: property list
                plistEval = arguments.car.eval(symbolTable);
                if (!(plistEval instanceof List))
                    throw new TypeException(arguments.car, List.class);

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
            environment,
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
                        if (fe != null) topics.add(fe.helpinfo());

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
                    + "returned by the else-form."))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments) 
            throws LispException {
                if (arguments == null || arguments.length() < 2)
                    throw new InvalidArgumentQuantityException(
                        2, arguments == null ? 0 : arguments.length());

                // evaluate test form
                SExp testResult = arguments.car.eval(symbolTable);

                // advance to then-form
                arguments = arguments.cdr;

                // if false, advance to else-form
                if (testResult == null) arguments = arguments.cdr;

                if (arguments == null) return arguments;
                return arguments.eval(symbolTable);
            }
        };

        // ---
        // LET
        // ---

        final SpecialFormEntry LET = new SpecialFormEntry(
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
                "result", "the value returned by the last form"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {
                
                SymbolTable newScope;
                Seq letBinding;
                ArrayList<Symbol> symbols = new ArrayList<Symbol>();
                ArrayList<SExp> values = new ArrayList<SExp>();
                SExp retVal = SExp.NIL;

                if (arguments == null)
                    throw new InvalidArgumentQuantityException(0);

                if (!(arguments.car instanceof List))
                    throw new LispException("Malformed LET bindings: "
                        + arguments.car.toString());

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
                "result", "the value returned by the last form"))
        {
            public SExp call(SymbolTable symbolTable, Seq arguments)
            throws LispException {

                SymbolTable newScope;
                Seq letBinding;
                Symbol var;
                SExp initvalue;
                SExp retVal = SExp.NIL;

                if (arguments == null)
                    throw new InvalidArgumentQuantityException(0);

                if (!(arguments.car instanceof List))
                    throw new LispException("Malformed LET bindings: "
                        + arguments.car.toString());

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
            environment,
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

        // -----
        // QUOTE
        // -----

        final SpecialFormEntry QUOTE = new SpecialFormEntry(
            environment,
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
                    throw new InvalidArgumentQuantityException(1, 0);

                return arguments.car;
            }
        };

        // -----
        // PROGN
        // -----

        final SpecialFormEntry PROGN = new SpecialFormEntry(
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
                "form", "a form"))
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

        environment.globalSymbolTable.bind(new Symbol("-"), DIF);
        environment.globalSymbolTable.bind(new Symbol("/"), DIV);
        environment.globalSymbolTable.bind(new Symbol("*"), MUL);
        environment.globalSymbolTable.bind(new Symbol("+"), SUM);
        environment.globalSymbolTable.bind(new Symbol("CONS"), CONS);
        environment.globalSymbolTable.bind(new Symbol("DEFUN"), DEFUN);
        environment.globalSymbolTable.bind(new Symbol("DEFPARAMETER"), DEFPARAMETER);
        environment.globalSymbolTable.bind(new Symbol("DEFVAR"), DEFVAR);
        environment.globalSymbolTable.bind(new Symbol("ENABLE-DEBUG-AST"), ENABLEDEBUGAST);
        environment.globalSymbolTable.bind(new Symbol("GETF"), GETF);
        environment.globalSymbolTable.bind(new Symbol("HELP"), HELP);
        environment.globalSymbolTable.bind(new Symbol("IF"), IF);
        environment.globalSymbolTable.bind(new Symbol("LET"), LET);
        environment.globalSymbolTable.bind(new Symbol("LET*"), LET_STAR);
        environment.globalSymbolTable.bind(new Symbol("LIST"), LIST);
        environment.globalSymbolTable.bind(new Symbol("QUOTE"), QUOTE);
        environment.globalSymbolTable.bind(new Symbol("PROGN"), PROGN);
        environment.globalSymbolTable.bind(new Symbol("SETQ"), SETQ);
        environment.globalSymbolTable.bind(new Symbol("TRACE"), TRACE);
    }
}

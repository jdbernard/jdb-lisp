package edu.utexas.cs345.jdblisp;

/**
 * FunctionEntry
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class FunctionEntry implements FormEntry {

    public final HelpTopic helpinfo;
    public final Symbol name;
    protected final Symbol[] parameters;
    protected final SExp body;

    protected boolean traceEnabled;

    //private Logger traceLog = Logger.getLogger(getClass());

    public FunctionEntry(Symbol name, Symbol[] parameters, SExp body) {

        // build invocation help string
        String invokation = "(" + name.name;
        for (Symbol param : parameters) invokation += " <" + param.name + ">";
        invokation += ")";
        String bodyInfo = "Function body: " + body.toString();

        // build help topic
        FormHelpTopic helpinfo = new FormHelpTopic(name.name, null, invokation, bodyInfo);

        this.name = name;
        this.parameters = parameters;
        this.body = body;
        this.helpinfo = helpinfo;
    }

    public FunctionEntry(Symbol name, Symbol[] parameters, SExp body,
    HelpTopic helpinfo) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
        this.helpinfo = helpinfo;
    }

    public boolean isTraceEnabled() { return traceEnabled ;}

    public void enableTrace(boolean enable) { this.traceEnabled = enable; }

    public SExp call(SymbolTable symbolTable, Seq arguments) throws LispException {

        String traceString = null;
        SExp evaluatedArg, retVal;

        if (traceEnabled)
            traceString = "(" + name.name;

        // bind arguments to parameters
        SymbolTable localScope = new SymbolTable(symbolTable);
        int i = 0;
        while (i < parameters.length) {

            // too few arguments
            if (arguments == null)
                throw new InvalidArgumentQuantityException(
                    parameters.length, i);

            // bind one arg to param
            evaluatedArg = arguments.car.eval(symbolTable);
            localScope.bind(parameters[i], new VariableEntry(evaluatedArg));

            if (traceEnabled) traceString += " " + evaluatedArg.toString();

            arguments = arguments.cdr;
            ++i;
        }

        // too many arguments
        if (arguments != null)
            throw new InvalidArgumentQuantityException(parameters.length,
                (i + arguments.length()));

        if (traceEnabled) traceString += ")";
        if (traceEnabled) System.out.println(traceString);


        retVal = body.eval(localScope);

        if (traceEnabled)
            traceString = name.name + " returned " + retVal.toString();
        if (traceEnabled) System.out.println(traceString);

        return retVal;
    }

    public HelpTopic helpinfo() { return helpinfo; }
}

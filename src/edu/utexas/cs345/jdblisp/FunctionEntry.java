package edu.utexas.cs345.jdblisp;

/**
 * FunctionEntry
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class FunctionEntry extends FormEntry {

    protected final Symbol[] parameters;
    protected final SExp body;

    protected boolean traceEnabled;

    //private Logger traceLog = Logger.getLogger(getClass());

    public FunctionEntry(Symbol symbol, Symbol[] parameters, SExp body) {
        super(symbol, null);

        // build invocation help string
        String invokation = "(" + symbol.name;
        for (Symbol param : parameters) invokation += " <" + param.name + ">";
        invokation += ")";
        String bodyInfo = "Function body: " + body.toString();

        // build help topic
        FormHelpTopic helpinfo = new FormHelpTopic(symbol.name, null, invokation, bodyInfo);

        this.parameters = parameters;
        this.body = body;
        this.helpinfo = helpinfo;
    }

    public FunctionEntry(Symbol symbol, Symbol[] parameters, SExp body,
    HelpTopic helpinfo) {
        super(symbol, helpinfo);
        this.parameters = parameters;
        this.body = body;
    }

    public boolean isTraceEnabled() { return traceEnabled ;}

    public void enableTrace(boolean enable) { this.traceEnabled = enable; }

    public String display(String offset) {
        return offset + "Function: " + symbol.toString();
    }

    public String toString() { return "<FUNCTION " + symbol.toString() + ">"; }

    public SExp call(SymbolTable symbolTable, Seq arguments) throws LispException {

        String traceString = null;
        SExp evaluatedArg, retVal;

        if (traceEnabled)
            traceString = "(" + symbol.name;

        // bind arguments to parameters
        SymbolTable localScope = new SymbolTable(symbolTable);
        int i = 0;
        while (i < parameters.length) {

            // too few arguments
            if (arguments == null)
                throw new InvalidArgumentQuantityException(
                    toString(), parameters.length, i);

            // bind one arg to param
            evaluatedArg = arguments.car.eval(symbolTable);
            localScope.bind(parameters[i], new VariableEntry(parameters[i],
                evaluatedArg));

            if (traceEnabled) traceString += " " + evaluatedArg.toString();

            arguments = arguments.cdr;
            ++i;
        }

        // too many arguments
        if (arguments != null)
            throw new InvalidArgumentQuantityException(
                toString(),parameters.length, (i + arguments.length()));

        if (traceEnabled) traceString += ")";
        if (traceEnabled) System.out.println(traceString);


        retVal = body.eval(localScope);

        if (traceEnabled)
            traceString = symbol.name + " returned " + retVal.toString();
        if (traceEnabled) System.out.println(traceString);

        return retVal;
    }

    public HelpTopic helpinfo() { return helpinfo; }
}

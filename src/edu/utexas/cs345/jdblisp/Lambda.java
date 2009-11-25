package edu.utexas.cs345.jdblisp;

/**
 * Lambda
 */
public class Lambda extends FunctionEntry {

    private SymbolTable closure;

    public Lambda(Symbol[] parameters, SExp body, SymbolTable closure) {
        super(new Symbol(""), parameters, body);

        this.closure = closure;
    }

    public Lambda(Symbol[] parameters, SExp body, SymbolTable closure,
    HelpTopic helpinfo) {
        super(new Symbol(""), parameters, body, helpinfo);

        this.closure = closure;
    }

    public String display(String offset) {
        StringBuilder sb = new StringBuilder();
        sb.append(offset).append("Lambda : \n");

        for (Symbol param : parameters)
            sb.append("  ").append(offset).append("Parameter: \n")
                .append(param.display(offset + "  "));

        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<LAMBDA (");
        for (Symbol param : parameters)
            sb.append(param.toString()).append(" ");
        sb.append(") >");

        return sb.toString();
    }

    @Override
    public SExp call(SymbolTable symbolTable, Seq arguments)
    throws LispException {
        return super.call(closure, arguments);
    }
}

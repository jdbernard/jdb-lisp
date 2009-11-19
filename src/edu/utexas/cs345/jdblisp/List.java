package edu.utexas.cs345.jdblisp;

/**
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class List implements SExp {
    public final Seq seq;

    public List(Seq seq) {
        this.seq = seq;
    }

    /** {@inheritdoc}*/
    public SymbolTable eval(SymbolTable table) throws LispException {
        // if the car of the sequence is a symbol, 
        if (seq.car instanceof Symbol) {
            // then that symbol is the name of an operator
            // Depending on the funciton binding of the operator in the
            // current lexical environment the form is either:
            // a special form
            // a macro form or
            // a function form

            // look up in the symbol table
            TableEntry functionEntry = table.findFunction((Symbol) seq.car);

            // check parameter length
            if (functionEntry.parameters.length != seq.cdr.length())
                throw new LispException("Invalid number of parameters: "
                    + seq.cdr.length());

            // create a new SymbolTable and bind arguments to parameters
            SymbolTable newTable = table;
            Seq nextArg = seq.cdr;
            for (Symbol parameter : functionEntry.parameters) {
                TableEntry newEntry = new TableEntry(parameter, null, nextArg.car);
                newTable = new SymbolTable(newEntry, newTable);
                nextArg = nextArg.cdr;
            }

            // call function
            return functionEntry.body.eval(newTable);

        }

        return null;
    }

    public String display(String offset) {
        StringBuilder sb = new StringBuilder();
        sb.append(offset);
        sb.append("List: \n");
        if (seq == null) sb.append(offset + "  NIL\n");
        else sb.append(seq.display(offset + "  "));
        return sb.toString();
    }

}

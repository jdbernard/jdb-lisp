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
            FunctionEntry functionEntry = table.lookupFunction((Symbol) seq.car);

            // call function
            return functionEntry.call(table);

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

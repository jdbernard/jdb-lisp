package edu.utexas.cs345.jdblisp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import edu.utexas.cs345.jdblisp.parser.Parser;
import edu.utexas.cs345.jdblisp.parser.ParseException;

/**
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class Lisp {

    private Parser parser;
    private SymbolTable globalSymbolTable;
    private boolean interactive = true;

    public Lisp() {
        this(true);
    }

    public Lisp(boolean interactive) {
        this.interactive = interactive;
        Parser parser = new Parser(new ByteArrayInputStream(new byte[]{}));
    }

    public static void main(String[] args) {
        Lisp lisp = new Lisp();
        lisp.repl(System.in, System.out);
        globalSymbolTable = SpecialForms.createSymbolTable();
    }

    public void repl(InputStream is, OutputStream os) {

        // wrap input and output in more friendly objects
        Scanner in = new Scanner(is);
        PrintWriter out = new PrintWriter(os);

        // print prompt if applicable
        if (interactive) {
            out.print("> ");
            out.flush();
        }

        // read each line of input
        while(in.hasNextLine()) {

            // get line
            String line = in.nextLine();

            // stuff it into an input buffer for the parser
            ByteArrayInputStream bin = new ByteArrayInputStream(line.getBytes());

            // re-init the parser with the new stream
            parser.ReInit(bin);

            // parse the line
            SExp sexp;
            try { sexp = parser.sexp(); }
            catch (ParseException pe) {
                // TODO
                out.println(pe.getLocalizedMessage());
                continue;
            }

            out.println(sexp.display("  "));
            out.println(sexp.eval(globalSymbolTable).car.body);

            // print prompt if applicable
            if (interactive) {
                out.print("> ");
                out.flush();
            }

        }
    }

}

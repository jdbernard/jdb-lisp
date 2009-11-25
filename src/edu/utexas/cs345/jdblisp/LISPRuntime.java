package edu.utexas.cs345.jdblisp;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import edu.utexas.cs345.jdblisp.parser.Parser;
import edu.utexas.cs345.jdblisp.parser.ParseException;

/**
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class LISPRuntime {

    public static final String VERSION = "0.1.0";

    public SymbolTable globalSymbolTable;

    private Parser parser;

    private boolean interactive = true;
    private boolean stop = false;

    boolean dumpAST = false;

    private OutputStream os;

    public LISPRuntime() {
        this(true);
    }

    public LISPRuntime(boolean interactive) {
        this.interactive = interactive;
        Parser parser = new Parser(new ByteArrayInputStream(new byte[]{}));

        // build global constants
        SymbolTable constantsSymbolTable = defineGlobalConstants();

        // build global symbol table
        globalSymbolTable = new SymbolTable(constantsSymbolTable);
        SpecialFormEntry.defineSpecialForms(this);
    }

    // TODO: is this needed? 
    // public void setInteractive(boolean interactive) { this.interactive = interactive; }

    public static void main(String[] args) throws IOException {

        // create the LISP environment
        LISPRuntime lisp = new LISPRuntime();
        
        // print welcome message
        System.out.println("Welcome to JDB-Lisp v" + VERSION + ", a subset of Common Lisp.");;
        System.out.println("On-line help is available via the (help funcname) command.");
        // parse command-line arguments (treat non-options as input files)
        // TODO: replace with Apache Commons CLI
        for (String arg : args) {
            System.out.println("Loading file '" + arg + "...");
            lisp.interactive = false;
            lisp.repl(new FileInputStream(arg), System.out);
        }

        // drop into interactive mode
        lisp.interactive = true;
        lisp.repl(System.in, System.out);
    }

    public void repl(InputStream is, OutputStream os) throws IOException {

        // wrap output in a more friendly object
        this.os = os;
        PrintWriter out = new PrintWriter(os,true);

        parser.ReInit(is);
        SExp sexp;
        while (!stop) {

            // print prompt if applicable
            if (interactive) {
                out.print("> ");
                out.flush();
            }

            try { sexp = parser.sexp(); }
            catch (ParseException pe) {

                // using the available call to check if the stream is open.
                try { is.available(); }             // if an exception is
                catch (IOException ioe) { break; }  // then it is closed
            
                out.println(pe.getLocalizedMessage());
                continue;
            }

            if (dumpAST) {
                out.println("ABSTRACT SYNTAX:");
                out.println(sexp.display("  "));
                out.println("----------------");
            }

            if (interactive) {
                try {
                    SExp result = sexp.eval(globalSymbolTable);
                    out.println(result == null ? "NIL" : result.toString());
                } catch (LispException le) {
                    out.println(le.getLocalizedMessage());
                    continue;
                }
            }

        }

        out.println("\nLeaving JDB-LISP");
    }

    OutputStream getOutputStream() { return os; }

    void signalStop() { stop = true; }

    private static SymbolTable defineGlobalConstants() {
        SymbolTable constantsTable = new SymbolTable();
        constantsTable.bind(new Symbol("T"), new VariableEntry(SExp.T, true));
        constantsTable.bind(new Symbol("NIL"), new VariableEntry(SExp.NIL, true));

        return constantsTable;
    }

}

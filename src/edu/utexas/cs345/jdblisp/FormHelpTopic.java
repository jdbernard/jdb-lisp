package edu.utexas.cs345.jdblisp;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * FormHelpTopic
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class FormHelpTopic extends HelpTopic {

    protected final String invokation;
    protected final String[][] parameters;

    /**
     * TODO
     */
    public FormHelpTopic(String operatorName, String shortDescription,
    String invokation, String longDescription, String... parameterDefinitions) {
        super(operatorName, shortDescription, longDescription);
        this.invokation = invokation;

        assert (parameterDefinitions.length % 2 == 0);

        ArrayList<String[]> paramDefs = new ArrayList<String[]>();
        for (int i = 0; i < parameterDefinitions.length; i+=2) {
            paramDefs.add(new String[] {
                parameterDefinitions[i],
                parameterDefinitions[i+1] });
        }

        parameters = paramDefs.toArray(new String[][] {});
    }

    @Override
    public void print(OutputStream os, int wrapWidth) {
        WrappedPrinter out = new WrappedPrinter(os, wrapWidth, true);

        out.print(name + ": ");
        if (shortDescription != null)
            out.println(shortDescription, "    ");
        out.println();
        out.println(invokation, "    ");
        out.println();
        if (body != null) {
            out.println(body, "    ");
            out.println();
        }

        for (String[] param : parameters) {
            out.print(param[0] + "\t", "    ");
            out.println(param[1], "        ");
        }
    }
}

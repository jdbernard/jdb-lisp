package edu.utexas.cs345.jdblisp;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * HelpTopic
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class HelpTopic {

    public static Map<String, HelpTopic> helpTopics;

    protected final String name;
    protected final String shortDescription;
    protected final String body;

    public HelpTopic(String name, String shortDescription, String body) {
        this.name = name;
        this.shortDescription = shortDescription;
        this.body = body;
    }

    public void print(OutputStream os) { print(os, 79); }

    public void print(OutputStream os, int wrapWidth) {
        WrappedPrinter out = new WrappedPrinter(os, wrapWidth, true);
        out.println(name);
        out.println(shortDescription, "    ");
        out.println();
        out.println(body, "    ");
    }

    class WrappedPrinter {

        private PrintWriter printer;
        private int wrapWidth;
        private int lastPrintedLineLength = 0;

        public WrappedPrinter(OutputStream os, int wrapWidth,
        boolean autoflush) {
            this(new PrintWriter(os, autoflush), wrapWidth);
        }

        public WrappedPrinter(PrintWriter pw, int wrapWidth) {
            this.printer = pw;
            this.wrapWidth = wrapWidth;
        }

        public void print(String message) { print(message, ""); }

        public void print(String message, String offset) {
            int lastSpaceIdx = 0;
            int curLineLength = 0;
            int lineStartIdx = 0;
            int i = 0;
            int actualWidth = wrapWidth - offset.length();

            //message = message.replaceAll("[\n\r]", " ");

            // print initial offset if this is the beginning of the line
            if (lastPrintedLineLength == 0) {
                printer.print(offset);
                curLineLength = offset.length();
            }


            for (i = 0; i < message.length(); ++i) {

                curLineLength++;
                if (message.charAt(i) == '\t') curLineLength += 7;

                // line has overflowed the prescribed width
                if (curLineLength > actualWidth) {
                    // print up to the last space before the overflow
                    printer.println(message.substring(lineStartIdx, lastSpaceIdx));

                    // pick up the next line after said space
                    lineStartIdx = lastSpaceIdx + 1;
                    i = lastSpaceIdx;
                    curLineLength = 0;

                    // print initial offset if there is still more to print
                    if (lineStartIdx < message.length()) {
                        printer.print(offset);
                        curLineLength = offset.length();
                    }
                }

                // see whitespace, update last space index
                if (Character.isWhitespace(message.charAt(i))) lastSpaceIdx = i;
            }

            // any left over, it will fit on one line
            if (i - lineStartIdx > 0) {
                String lastLine = message.substring(lineStartIdx);
                printer.print(lastLine);
                curLineLength += lastLine.length();
            }

            // save back the new position
            lastPrintedLineLength = curLineLength;

        }

        public void println() { println("", ""); }

        public void println(String message) { println(message, ""); }

        public void println(String message, String offset) {

            print(message, offset);
            printer.println();
            lastPrintedLineLength = 0;
        }

    }
}

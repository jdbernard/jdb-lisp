package edu.utexas.cs345.jdblisp;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class Num implements SExp {

    private Number n;

    public Num(String string) {
        try { n = Short.parseShort(string); return; }
        catch (NumberFormatException nfe) {}

        try { n = Integer.parseInt(string); return; }
        catch (NumberFormatException nfe) {}

        try { n = Long.parseLong(string); return; }
        catch (NumberFormatException nfe) {}

        try { n = new BigInteger(string); return; }
        catch (NumberFormatException nfe) {}

        try { n = Float.parseFloat(string); return; }
        catch (NumberFormatException nfe) {}

        try { n = Double.parseDouble(string); return; }
        catch (NumberFormatException nfe) {}

        try { n = new BigDecimal(string); return; }
        catch (NumberFormatException nfe) {
            throw new LispException("Cannot parse number: " + string);
        }
    }

    /** {@inheritdoc} */
    public SExp eval(SymbolTable table) {
        return new SymbolTable(
            new TableEntry(
                new Symbol("RETURN-VAL"),
                null,
                this));
    }

    public String display(String offset) {
        return offset + "Num: " + n.toString() + "\n";
    }

    @Override
    public String toString() {
        return n.toString();
    }
}

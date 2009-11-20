package edu.utexas.cs345.jdblisp;

/**
 * Seq
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class Seq implements SExp {

    public final SExp car;
    public final Seq cdr;

    public Seq(SExp car, Seq cdr) {
        assert (car != null);
        this.car = car;
        this.cdr = cdr;
    }

    public Seq(SExp... elements) {
        int idx = elements.length;
        Seq last = null;
        while (idx > 1) last = new Seq(elements[--idx], last);

        this.car = elements[0];
        this.cdr = last;
    }

    /** {@inheritdoc}*/
    public SymbolTable eval(SymbolTable table) {
        assert(false) : "Attempted to eval() a Seq.";
        throw new UnsupportedOperationException("Attempted to eval a Seq.");
    }

    public int length() {
        if (cdr == null) return 1;
        return 1 + cdr.length();
    }

    public String display(String offset) {
        StringBuilder sb = new StringBuilder();
        sb.append(offset);
        sb.append("Seq: \n");
        
        sb.append(car.display(offset + "  "));

        if (cdr != null) sb.append(cdr.display(offset));

        return sb.toString();
    }

    @Override
    public String toString() {
        return car.toString() + (cdr == null ? "" : cdr.toString());
    }
}

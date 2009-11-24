package edu.utexas.cs345.jdblisp;

/**
 * Cons
 * @author Jonathan Bernard (jdbernard@gmail.com)
 */
public class Cons extends Seq {

    public final SExp cdr;

    public Cons(SExp car, SExp cdr) {
        super(car, (cdr == null ||
                    cdr == SExp.NIL ||
                    !(cdr instanceof Seq) ?
                        null : (Seq) cdr));
        this.cdr = (cdr == SExp.NIL ? null : cdr);
    }

    public Cons(SExp car, List list) {
        super(car, list.seq);
        this.cdr = list.seq;
    }

    @Override
    public String display(String offset) {
        if (this.cdr == super.cdr)
            return super.display(offset);

        StringBuilder sb = new StringBuilder();
        sb.append(offset);
        sb.append("Cons: \n");
        
        sb.append(car.display(offset + "  "));

        if (cdr != null) sb.append(cdr.display(offset));

        return sb.toString();
    }

    @Override
    public String toString() {
        if (this.cdr == super.cdr) return super.toString();

        else return car.toString() + " . " + cdr.toString();
    }
}

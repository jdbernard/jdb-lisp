package org.jdbernard.util;

import java.io.OutputStream;

/**
 * NullOutputStream
 * @author Jonathan Bernard (jdbernard@gmail.com)
 * All output directed to this OutputStream is ignored and lost. Writing to
 * stream is like writing to /dev/null.
 */
public class NullOutputStream extends OutputStream {

    public void write(int b) {};
}

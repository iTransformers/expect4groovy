package net.itransformers.expect4groovy.cliconnection.utils;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

public class TeeOutputStream extends FilterOutputStream {
    Logger logger = Logger.getLogger(TeeOutputStream.class.getName());
    private final OutputStream branch;
    private final boolean closeBranch;

    public TeeOutputStream(OutputStream output, OutputStream branch) {
        this(output, branch, false);
    }

    public TeeOutputStream(OutputStream output, OutputStream branch, boolean closeBranch) {
        super(output);
        this.branch = branch;
        this.closeBranch = closeBranch;
    }

    public void close() throws IOException {
        logger.info("Closing output stream.");
        try {
            super.close();
        } finally {
            if (closeBranch) {
                branch.close();
            }
        }
    }

    @Override
    public void write(int b) throws IOException {
        super.write(b);
        branch.write(b);
    }
}

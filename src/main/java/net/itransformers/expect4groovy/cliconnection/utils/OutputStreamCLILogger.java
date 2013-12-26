package net.itransformers.expect4groovy.cliconnection.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

public class OutputStreamCLILogger extends OutputStream {
    Logger logger = Logger.getLogger(OutputStreamCLILogger.class.getName());

    private ByteArrayOutputStream os;
    private boolean isOutputLogging;

    public OutputStreamCLILogger(boolean isOutputLogging) {
        this.isOutputLogging = isOutputLogging;
        os = new ByteArrayOutputStream();
    }
    public OutputStreamCLILogger(boolean isOutputLogging, int size) {
        this.isOutputLogging = isOutputLogging;
        os = new ByteArrayOutputStream(size);
    }

    @Override
    public void write(int b) throws IOException {
        if (b < 32){ // non printable characters
            switch (b) {
                case 0x0D : os.write(("[\\r]").getBytes()); break;
                case 0x09 : os.write(("[\\t]").getBytes()); break;
                case 0x0A : os.write(("[\\n]").getBytes()); break;
                case 0x0C : os.write(("[\\f]").getBytes()); break;
                case 0x07 : os.write(("[\\a]").getBytes()); break;
                case 0x1B : os.write(("[\\e]").getBytes()); break;
                default: os.write((String.format("[\\x%02X]",b)).getBytes());
            }
        } else { // printable characters
            os.write(b);
        }
        if (b == '\r') {
            if (isOutputLogging) {
                logger.info("<<< " + os.toString());
            } else {
                logger.info(">>> " + os.toString());
            }
            os.reset();
        }
    }
}

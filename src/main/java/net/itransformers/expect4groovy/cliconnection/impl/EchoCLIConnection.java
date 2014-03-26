package net.itransformers.expect4groovy.cliconnection.impl;

import net.itransformers.expect4groovy.cliconnection.CLIConnection;

import java.io.*;
import java.util.Map;

public class EchoCLIConnection implements CLIConnection{
    public static int BUFFER = 4*1024;
    PipedInputStream inputStream = new PipedInputStream(BUFFER);

    @Override
    public void connect(Map<String, String> params) throws IOException {

    }

    @Override
    public void disconnect() throws IOException {

    }

    @Override
    public InputStream inputStream() {
        return inputStream;
    }

    @Override
    public OutputStream outputStream() {
        try {
            return new PipedOutputStream(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

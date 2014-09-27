package net.itransformers.expect4groovy.cliconnection.impl;

import net.itransformers.expect4groovy.cliconnection.CLIConnection;

import java.io.*;
import java.util.Map;

public class EchoCLIConnection implements CLIConnection{
    public static int BUFFER = 4*1024;
    PipedInputStream inputStream = new PipedInputStream(BUFFER);
    private PipedOutputStream outputStream;

    @Override
    public void connect(Map<String, Object> params) throws IOException {
        outputStream = new PipedOutputStream(inputStream);
    }

    @Override
    public void disconnect() throws IOException {
        outputStream.close();
        inputStream.close();
    }

    @Override
    public InputStream inputStream() {
        return inputStream;
    }

    @Override
    public OutputStream outputStream() {
        return outputStream;
    }
}

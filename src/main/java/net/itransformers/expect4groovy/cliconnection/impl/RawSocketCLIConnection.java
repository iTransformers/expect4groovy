package net.itransformers.expect4groovy.cliconnection.impl;

import net.itransformers.expect4groovy.cliconnection.CLIConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Logger;

public class RawSocketCLIConnection implements CLIConnection {
    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Logger logger = Logger.getLogger(RawSocketCLIConnection.class.getName());

    public RawSocketCLIConnection() {
    }

    public void connect(Map<String, String> params) throws IOException {
        String address = params.get("address");
        if (address == null) {
            throw new RuntimeException("Missing parameter: address");
        }
        int colIndex = address.indexOf(":");
        if (colIndex == -1) {
            throw new RuntimeException("Invalid format of address parameter: "+address);
        }
        String host = address.substring(0, colIndex);
        String portStr = address.substring(colIndex+1);
        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException nfe){
            throw new RuntimeException("invalid format of port in address parameter: "+ address);
        }
        logger.info("Establishing connection ...");
        socket = new Socket(host, port);
        inputStream  = socket.getInputStream();
        outputStream  = socket.getOutputStream();
        logger.info("Connection established");
    }

    public InputStream inputStream(){
        return inputStream;
    }

    public OutputStream outputStream() {
        return outputStream;
    }

    public void disconnect() throws IOException {
        if (socket!=null){
            socket.close();
        }
    }

}

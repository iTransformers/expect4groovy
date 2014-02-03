package net.itransformers.expect4groovy.cliconnection.impl;

import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import net.itransformers.expect4groovy.cliconnection.CLIConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Map;

public class SshCLIConnection implements CLIConnection {
    public static final int DEFAULT_TIMEOUT = 60000;
    private ChannelShell channel;
    private InputStream inputStream;
    private OutputStream outputStream;

    public void connect(Map<String, String> params) throws IOException {
        String address = params.get("address");
        if (address == null) {
            throw new RuntimeException("Missing parameter: address");
        }
        String portStr = params.get("port");
        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException nfe){
            throw new RuntimeException("invalid format of port in address parameter: "+ address);
        }
        String user1 = params.get("username");
        if (user1 == null) {
            throw new RuntimeException("Missing parameter: username");
        }
        String password1 = params.get("password");
        if (password1 == null) {
            throw new RuntimeException("Missing parameter: password");
        }
        String timeoutStr = params.get("timeout");
        int timeout = DEFAULT_TIMEOUT;
        if (timeoutStr != null){
            timeout = Integer.parseInt(timeoutStr);
        }
        JSch jsch = new JSch();
        Session session = null;
        Hashtable<String,String> config = new Hashtable<String,String>();
        config.put("StrictHostKeyChecking", "no");
        try {
            session = jsch.getSession(user1, address, port);
            session.setPassword(password1);
            session.setConfig(config);
            session.connect(timeout);
            channel = (ChannelShell) session.openChannel("shell");
            inputStream = channel.getInputStream();
            outputStream = channel.getOutputStream();
            channel.connect();
        } catch (JSchException e) {
            throw new IOException(e);
        }

    }

    public InputStream inputStream() {
        return inputStream;
    }

    public OutputStream outputStream() {
        return outputStream;
    }

    public void disconnect() throws IOException {
        if (channel != null) channel.disconnect();
    }

}

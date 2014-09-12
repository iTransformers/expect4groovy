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
    Session session;
    private InputStream inputStream;
    private OutputStream outputStream;

    public void connect(Map<String, Object> params) throws IOException {
        String address = (String) params.get("address");
        if (address == null) {
            throw new RuntimeException("Missing parameter: address");
        }
        if (!(params.get("port") instanceof Integer)) {
            throw new RuntimeException("invalid format of port parameter: "+ address);
        }
        Integer port = (Integer)params.get("port");
        String user1 = (String) params.get("username");
        if (user1 == null) {
            throw new RuntimeException("Missing parameter: username");
        }
        String password1 = (String) params.get("password");
        if (password1 == null) {
            throw new RuntimeException("Missing parameter: password");
        }
        if (!( params.get("timeout") instanceof Integer)) {
            throw new RuntimeException("invalid format of timeout parameter: "+ address);
        }
        Integer timeout = (Integer)params.get("timeout");
        JSch jsch = new JSch();
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
        try { if (channel != null) channel.disconnect(); } catch (RuntimeException e) { e.printStackTrace();}
        try { if (session != null) session.disconnect(); } catch (RuntimeException e) { e.printStackTrace();}
    }

}

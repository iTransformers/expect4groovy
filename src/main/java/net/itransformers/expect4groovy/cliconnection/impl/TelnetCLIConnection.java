package net.itransformers.expect4groovy.cliconnection.impl;

import com.wittams.gritty.Questioner;
import net.itransformers.expect4groovy.cliconnection.CLIConnection;
import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class TelnetCLIConnection implements CLIConnection{

    private TelnetClient telnet = new TelnetClient();

    @Override
    public void connect(Map<String, String> params) throws IOException {
//        logger.info("Open telnet connection to: " + host + ":" + port);
        Questioner q = new Questioner() {
            @Override
            public String questionVisible(String s, String s1) {
                return null;
            }

            @Override
            public String questionHidden(String s) {
                return null;
            }

            @Override
            public void showMessage(String s) {}
        };
        try {
            if (!params.containsKey("port")) {
                throw new IllegalArgumentException("no port parameter is specified");
            }
            int port = Integer.parseInt(params.get("port"));
            telnet.connect(params.get("address"), port);
        } catch (IOException e) {
            e.printStackTrace();
            q.showMessage(e.getMessage());
        }
        telnet.setDefaultTimeout(2000);
    }

    @Override
    public void disconnect() throws IOException {
        telnet.disconnect();
    }

    @Override
    public InputStream inputStream() {
        return telnet.getInputStream();
    }

    @Override
    public OutputStream outputStream() {
        return telnet.getOutputStream();
    }

}

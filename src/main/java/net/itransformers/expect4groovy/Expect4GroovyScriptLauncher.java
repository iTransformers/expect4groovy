package net.itransformers.expect4groovy;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import net.itransformers.expect4java.cliconnection.CLIConnection;
import net.itransformers.expect4java.cliconnection.impl.EchoCLIConnection;
import net.itransformers.expect4java.cliconnection.impl.RawSocketCLIConnection;
import net.itransformers.expect4java.cliconnection.impl.SshCLIConnection;
import net.itransformers.expect4java.cliconnection.impl.TelnetCLIConnection;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Expect4GroovyScriptLauncher {
    Binding binding;
    CLIConnection connection;
    GroovyScriptEngine gse;
    static Logger logger = Logger.getLogger(Expect4GroovyScriptLauncher.class);

    public static void main(String[] args) throws IOException, ResourceException, ScriptException {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("protocol", "telnet");
        params.put("username", "lab");
        params.put("password", "lab123");
        params.put("enable-password", "lab123");
        params.put("address", "193.19.172.133");
        params.put("port", 11123);

        Expect4GroovyScriptLauncher launcher = new Expect4GroovyScriptLauncher();

        Map<String, Object> loginResult = launcher.open(new String[]{"conf/groovy/cisco/ios" + File.separator}, "cisco_login.groovy", params);


        if (loginResult.get("status").equals(2)) {
            logger.debug(loginResult);
        } else {
            Map<String, Object> cmdParams = new LinkedHashMap<String, Object>();
            cmdParams.put("evalScript", null);
            cmdParams.put("command","ip route 10.200.1.0 255.255.255.0 192.0.2.1");
            Map<String, Object> result = launcher.sendCommand("cisco_sendConfigCommand.groovy",cmdParams);
            params.put("configMode", result.get("configMode"));
            cmdParams.put("command","ip route 10.200.1.0 255.255.255.0 192.0.2.1");
            launcher.sendCommand("cisco_sendConfigCommand.groovy", cmdParams);
            launcher.close("cisco_logout.groovy");
        }
    }


    public Object launch(String[] roots, String scriptName, Map<String, Object> params) throws IOException, ResourceException, ScriptException {
        CLIConnection conn = createCliConnection(params);
        try {
            conn.connect(params);
            Binding binding = new Binding();
            Expect4Groovy.createBindings(conn, binding, true);
            binding.setProperty("params", params);
            GroovyScriptEngine gse = new GroovyScriptEngine(roots);
            return gse.run(scriptName, binding);
        } finally {
            conn.disconnect();
        }
    }

    public Map<String, Object> sendCommand(String scriptName, Map<String, Object> params) throws ResourceException, ScriptException {
        Map<String, Object> allParams = (Map<String, Object>) binding.getProperty("params");
        allParams.putAll(params); //merge params with the one obtained from the other commands
        binding.setProperty("params", allParams);
        Map<String, Object> result = (Map<String, Object>) gse.run(scriptName, binding);
        return result;

    }

    public Map<String, Object> open(String[] roots, String scriptName, Map<String, Object> params) throws ResourceException, ScriptException {
        connection = createCliConnection(params);
        Map<String, Object> result = null;
        try {
            connection.connect(params);
            binding = new Binding();
            Expect4Groovy.createBindings(connection, binding, true);
            binding.setProperty("params", params);
            gse = new GroovyScriptEngine(roots);
            result = (Map<String, Object>) gse.run(scriptName, binding);
            if (result.get("status").equals("1")) {
                return result;
            } else {
                return result;
            }

        } catch (IOException ioe) {
            logger.info(ioe);
        }
        return result;
    }

    public Map<String, Object> close(String scriptName) throws ResourceException, ScriptException {
        try {
            Map<String, Object> result = (Map<String, Object>) gse.run(scriptName, binding);
            return result;
        } finally {
            try {
                connection.disconnect();
            } catch (IOException e) {
                logger.info(e);
            }
        }
    }

    private CLIConnection createCliConnection(Map<String, Object> params) {
        CLIConnection conn;
        if ("telnet".equals(params.get("protocol"))) {
            conn = new TelnetCLIConnection();
        } else if ("raw".equals(params.get("protocol"))) {
            conn = new RawSocketCLIConnection();
        } else if ("echo".equals(params.get("protocol"))) {
            conn = new EchoCLIConnection();
        } else {
            conn = new SshCLIConnection();
        }
        return conn;
    }
}

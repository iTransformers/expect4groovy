package net.itransformers.expect4groovy;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import net.itransformers.expect4groovy.cliconnection.CLIConnection;
import net.itransformers.expect4groovy.cliconnection.impl.EchoCLIConnection;
import net.itransformers.expect4groovy.cliconnection.impl.RawSocketCLIConnection;
import net.itransformers.expect4groovy.cliconnection.impl.SshCLIConnection;
import net.itransformers.expect4groovy.cliconnection.impl.TelnetCLIConnection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Expect4GroovyScriptLauncher {

//    public static void main(String[] args) throws IOException, ResourceException, ScriptException {
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("protocol","telnet");
//        params.put("username","lab");
//        params.put("password","pass123");
//        params.put("enablePass","pass123");
//        params.put("address","10.17.1.51");
//        params.put("port","2021");
//        params.put("command","no ip domain-lookup");
//
//        Map<String, Object> result = new Expect4GroovyScriptLauncher().launch(new String[]{"src/test/java/"}, "expect4groovy_test.groovy", params);
//
//        System.out.println("Status: "+ result.get("status"));
//        System.out.println("Data"+ result.get("data"));
//
//    }

    public Object launch(String[] roots, String scriptName, Map<String, String> params) throws IOException, ResourceException, ScriptException {
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

    private CLIConnection createCliConnection(Map<String, String> params) {
        CLIConnection conn;
        if ("telnet".equals(params.get("protocol"))){
            conn = new TelnetCLIConnection();
        } else if ("raw".equals(params.get("protocol"))){
            conn = new RawSocketCLIConnection();
        } else if ("echo".equals(params.get("protocol"))){
            conn = new EchoCLIConnection();
        } else {
            conn = new SshCLIConnection();
        }
        return conn;
    }
}

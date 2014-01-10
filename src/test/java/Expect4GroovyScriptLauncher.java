import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import net.itransformers.expect4groovy.Expect4Groovy;
import net.itransformers.expect4groovy.cliconnection.CLIConnection;
import net.itransformers.expect4groovy.cliconnection.impl.RawSocketCLIConnection;
import net.itransformers.expect4groovy.cliconnection.impl.SshCLIConnection;
import net.itransformers.expect4groovy.cliconnection.impl.TelnetCLIConnection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Expect4GroovyScriptLauncher {

    public static void main(String[] args) throws IOException, ResourceException, ScriptException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("protocol","telnet");
        params.put("username","misho");
        params.put("password","misho321");
        params.put("enablePass","misho321");
        params.put("address","localhost");
        params.put("port","23");
        params.put("command","no ip domain-lookup");

        Map<String, String> result = new Expect4GroovyScriptLauncher().launch(new String[]{"src/test/java/"}, "expect4groovy_test.groovy", params);

        System.out.println("Status: "+ result.get("status"));
        System.out.println("Data"+ result.get("data"));

    }

    private Map<String,String> launch(String[] roots, String scriptName, Map<String, String> params) throws IOException, ResourceException, ScriptException {
        CLIConnection conn = createCliConnection(params);
        try {
            conn.connect(params);
            Binding binding = new Binding();
            Expect4Groovy.createBindings(conn, binding, true);
            binding.setProperty("params", params);
            GroovyScriptEngine gse = new GroovyScriptEngine(roots);
            Map<String, String> result = (Map<String, String>) gse.run(scriptName, binding);
            return result;
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
        }  else {
            conn = new SshCLIConnection();
        }
        return conn;
    }
}

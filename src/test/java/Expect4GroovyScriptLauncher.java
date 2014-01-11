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
        params.put("protocol","ssh");
        params.put("username","vvv");
        params.put("password","test");
        params.put("enablePass","test");
        params.put("address","localhost");
        params.put("port","22");
        params.put("command","no ip domain-lookup");

        Expect4GroovyScriptLauncher launcher = new Expect4GroovyScriptLauncher();
        Map<String, String> result = launcher.launch(new String[]{"src/test/java/"}, "ssh_expect4j.groovy", params);
        if (result != null) {
            Map<String, String> mapResult = (Map<String, String>) result;
            System.out.println("Status: "+ mapResult.get("status"));
            System.out.println("Data"+ mapResult.get("data"));
        }
        System.out.println("--------");
    }

    private Map<String,String> launch(String[] roots, String scriptName, Map<String, String> params) throws IOException, ResourceException, ScriptException {
        CLIConnection conn = createCliConnection(params);
        try {
            conn.connect(params);
            Binding binding = new Binding();
            Expect4Groovy.createBindings(conn, binding, true);
            binding.setProperty("params", params);
            GroovyScriptEngine gse = new GroovyScriptEngine(roots);
            Object result = gse.run(scriptName, binding);
            if (result instanceof Map) {
                return (Map)result;
            } else {
                return null;
            }
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

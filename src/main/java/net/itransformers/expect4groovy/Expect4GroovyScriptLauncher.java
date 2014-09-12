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
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Expect4GroovyScriptLauncher {
    Binding binding;
    CLIConnection connection;
    GroovyScriptEngine gse;
    static Logger logger = Logger.getLogger(Expect4GroovyScriptLauncher.class);

    public static void main(String[] args) throws IOException, ResourceException, ScriptException {

    Map<String, Object> params = new HashMap<String, Object>();
    params.put("protocol","telnet");
    params.put("username","lab");
    params.put("password","lab123");
    params.put("enable-password","lab123");
    params.put("address","193.19.172.133");
    params.put("port",11123);

    Expect4GroovyScriptLauncher launcher = new Expect4GroovyScriptLauncher();

        Map<String, Object> loginResult = launcher.open(new String[]{"conf/groovy/cisco/ios" + File.separator}, "cisco_login.groovy", params);
    if(loginResult.get("status").equals(2)){
        logger.debug(loginResult);
    }else{
        Map<String, Object> result = launcher.sendCommand("cisco_sendConfigCommand.groovy", "ip route 10.200.1.0 255.255.255.0 192.0.2.1",null);
        params.put("configMode", result.get("configMode"));
        launcher.sendCommand("cisco_sendConfigCommand.groovy", "ip route 10.210.1.0 255.255.255.0 192.0.2.1",null);
        launcher.close("cisco_logout.groovy");

    }
    }
    public static void main1(String[] args) throws IOException, ResourceException, ScriptException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("protocol","telnet");
        params.put("username","lab");
        params.put("password","lab123");
        params.put("enable-password","lab123");
        params.put("address","10.17.1.13");
        params.put("port",23);
        params.put("command","no ip domain-lookup");

        Expect4GroovyScriptLauncher launcher = new Expect4GroovyScriptLauncher();

        Map<String, Object> loginResult = launcher.open(new String[]{"/Users/niau/Projects/expect4groovy-expect4groovy/conf/groovy/cisco/ios" + File.separator}, "cisco_login.groovy", params);
        if(loginResult.get("status").equals(2)){
            logger.debug(loginResult);
        }else{
            Map<String, Object> result = null;
            result = launcher.sendCommand("cisco_sendCommand.groovy", "sh runn", "/conf/groovy/cisco/ios/cisco_config_eval.groovy");
            Map<String, Object> evalData = (Map<String, Object>) result.get("reportResult");
            StringBuilder sb = new StringBuilder();
            if(evalData!=null){
                for (String key : evalData.keySet()) {
                    sb.append("\n\t<entry>\n");
                    sb.append("\t\t<AuditRule>" + key + "</AuditRule>" + "\n");
                    Map<String,Map<String,Integer>> keyMap= (Map<String, Map<String,Integer>>) evalData.get(key);
                    if(keyMap.get("message")!=null){
                        sb.append("\t\t<Statement><![CDATA[" + keyMap.get("message") + "]]></Statement>" + "\n");
                    }

                    if(keyMap.get("score")!=null){

                        sb.append("\t\t<Score>"+keyMap.get("score")+"</Score>"+"\n");
                    }
                    sb.append("\t</entry>");
                    logger.debug(key + " " + evalData.get(key));
                }
            }else{


            }
            launcher.close("cisco_login.groovy");

        }


//        Map<String, Object> result = (Map<String, Object>) new Expect4GroovyScriptLauncher().launch(new String[]{"src/test/java/"}, "expect4groovy_test.groovy", params);
//        if(result!=null) {
//            System.out.println("Status: " + result.get("status"));
//            System.out.println("Data" + result.get("data"));
//        }else{
//            System.out.println("No result!");
//        }
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
    public Map<String,Object> sendCommand(String scriptName, String command,String evalscriptPath) throws ResourceException, ScriptException {
        binding.setProperty("command",command);
        if(evalscriptPath!=null){
            binding.setProperty("evalScript",new File(evalscriptPath));
        }else {
            binding.setProperty("evalScript",null);

        }
        Map<String, Object> result = (Map<String, Object>) gse.run(scriptName, binding);
        return result;

    }

    public Map<String,Object>  open(String[] roots, String scriptName, Map<String, Object> params) throws ResourceException, ScriptException {
        connection = createCliConnection(params);
        Map<String, Object> result = null;
        try {
            connection.connect(params);
            binding = new Binding();
            Expect4Groovy.createBindings(connection, binding, true);
            binding.setProperty("params", params);
            gse = new GroovyScriptEngine(roots);
            result = (Map<String, Object>) gse.run(scriptName, binding);
            if (result.get("status").equals("1")){
                return result;
            }else {
                return result;
            }

        } catch (IOException ioe){
            logger.info(ioe);
        }
        return result;
    }

    public Map<String, Object> close(String scriptName) throws ResourceException, ScriptException {
        try {
            Map<String, Object> result = (Map<String, Object>) gse.run(scriptName, binding);

//            String status  = result.get("status");
            // if (status == 1)){
            connection.disconnect();
            return result;
            //}

        } catch (IOException ioe){
            logger.info(ioe);
        }
        return null;
    }

    private CLIConnection createCliConnection(Map<String, Object> params) {
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

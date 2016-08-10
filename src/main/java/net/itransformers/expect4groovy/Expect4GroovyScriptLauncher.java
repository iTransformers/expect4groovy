/*
 * Expect4GroovyScriptLauncher.java
 *
 * Copyright 2016  iTransformers Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.itransformers.expect4groovy;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import net.itransformers.expect4java.cliconnection.CLIConnection;
import net.itransformers.expect4java.cliconnection.impl.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;

public class Expect4GroovyScriptLauncher {
    Binding binding;
    CLIConnection connection;
    GroovyScriptEngine gse;
    static Logger logger = Logger.getLogger(Expect4GroovyScriptLauncher.class);


    public Object launch(String[] roots, String scriptName, Map<String, Object> params) throws IOException, ResourceException, ScriptException {
        CLIConnection conn = createCliConnection(params);
        return launch(roots, scriptName, params, conn);
    }

    public Object launch(String[] roots, String scriptName, Map<String, Object> params, CLIConnection conn) throws IOException, ResourceException, ScriptException {
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

//    public Map<String, Object> open(String[] roots, String scriptName, Map<String, Object> params) throws ResourceException, ScriptException {
//        connection = createCliConnection(params);
//        return open(roots,scriptName,params,connection);
//    }
//
//    public Map<String, Object> open(String[] roots, String scriptName, Map<String, Object> params, CLIConnection connection) throws ResourceException, ScriptException {
//        Map<String, Object> result = null;
//        try {
//            connection.connect(params);
//            binding = new Binding();
//            Expect4Groovy.createBindings(connection, binding, true);
//            binding.setProperty("params", params);
//            gse = new GroovyScriptEngine(roots);
//            result = (Map<String, Object>) gse.run(scriptName, binding);
//            if (result.get("status").equals("1")) {
//                return result;
//            } else {
//                return result;
//            }
//
//        } catch (IOException ioe) {
//            logger.info(ioe);
//        }
//        return result;
//    }

    public Map<String, Object> close(String scriptName,Map<String, Object> params) throws ResourceException, ScriptException {
        try {
            Map<String, Object> allParams = (Map<String, Object>) binding.getProperty("params");

            binding.setProperty("params", allParams);
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

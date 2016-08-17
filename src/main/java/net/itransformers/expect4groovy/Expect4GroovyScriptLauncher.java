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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Expect4GroovyScriptLauncher {
    Binding binding;
    CLIConnection connection;
    GroovyScriptEngine gse;
    static Logger logger = Logger.getLogger(Expect4GroovyScriptLauncher.class);


    public Object launchWithSimulator(List<String> roots, String scriptName, Map<String, Object> params,
                                      String simulatorName, Map<String, Object> simulatorParams) throws IOException, ResourceException, ScriptException {
        CLIConnection conn = new CrossPipedCLIConnection();
        CLIConnection simConn = new CrossPipedCLIConnection();

        simulatorParams.put("input",conn.inputStream());

        if (params.containsKey("input")) {
            throw new IllegalArgumentException("input is reserved parameter");
        }
        params.put("input",simConn.inputStream());

        conn.connect(params);
        simConn.connect(simulatorParams);

        Binding binding = new Binding();
        Expect4Groovy.createBindings(conn, binding, true);
        binding.setProperty("params", params);
        GroovyScriptEngine gse = new GroovyScriptEngine(roots.toArray(new String[roots.size()]));

        Binding simBinding = new Binding();
        Expect4Groovy.createBindings(simConn, simBinding, true);
        simBinding.setProperty("params", simulatorParams);
        GroovyScriptEngine simGse = new GroovyScriptEngine(roots.toArray(new String[roots.size()]));

        (new Thread(() -> {
            try {
                simGse.run(simulatorName, simBinding);
            } catch (ResourceException | ScriptException e) {
                e.printStackTrace();
            } finally {
                try {
                    simConn.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        })).start();
        try {
            return gse.run(scriptName, binding);
        } finally {
            conn.disconnect();
        }
    }

    public Object launch(List<String> roots, String scriptName, Map<String, Object> params) throws IOException, ResourceException, ScriptException {
        CLIConnection conn = createCliConnection(params);
        try {
            conn.connect(params);
            Binding binding = new Binding();
            Expect4Groovy.createBindings(conn, binding, true);
            binding.setProperty("params", params);
            GroovyScriptEngine gse = new GroovyScriptEngine(roots.toArray(new String[roots.size()]));
            return gse.run(scriptName, binding);
        } finally {
            conn.disconnect();
        }
    }

    private CLIConnection createCliConnection(Map<String, Object> params) {
        CLIConnection conn = null;
        if ("telnet".equals(params.get("protocol"))) {
            conn = new TelnetCLIConnection();
        } else if ("raw".equals(params.get("protocol"))) {
            conn = new RawSocketCLIConnection();
        } else if ("echo".equals(params.get("protocol"))) {
            conn = new EchoCLIConnection();
        } else if ("ssh".equals("protocol")){
            conn = new SshCLIConnection();
        }
        return conn;
    }
}

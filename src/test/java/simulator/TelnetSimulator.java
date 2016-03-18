/*
 * TelnetSimulator.java
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

package simulator;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;
import net.itransformers.expect4groovy.Expect4Groovy;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class TelnetSimulator {

    public static void main(String[] args) throws IOException, ScriptException, ResourceException {
        Logger logger = Logger.getLogger(TelnetSimulator.class.getName());
        logger.setLevel(Level.ALL);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter());
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);

        Map<String, String> params = parseCmdParams(args);
        String port = params.get("-p");
        String scriptsPaths = params.get("-d");
        String scriptName = params.get("-f");
        if (scriptName == null) {
            printUsage();
            System.exit(-1);
        }
        if (scriptsPaths == null) {
            scriptsPaths = ".";
        }
        if (port == null) {
            port = "23";
        }

        String[] roots = scriptsPaths.split(",");
        for (int i=0; i< roots.length; i++) {
            if (!roots[i].endsWith("/")) {
                roots[i] = roots[i] + "/";
            }
        }
        GroovyScriptEngine gse = null;
        try {
            gse = new GroovyScriptEngine(roots);
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return;
        }

        final ServerSocket ssocket = new ServerSocket(Integer.parseInt(port));
        System.out.println( "Server started on port: "+port);
        while (true) {
            Socket socket = null;
            try {
                System.out.println("Accepting new connection");
                socket = ssocket.accept();
                System.out.println("New connection created");
                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();
                Binding binding = new Binding();
                Map<String, Object> parameters = Expect4Groovy.createBindings(
                        new InputStreamReader(is),new OutputStreamWriter(os));
                for (Map.Entry<String, Object> stringStringEntry : parameters.entrySet()) {
                    binding.setProperty(stringStringEntry.getKey(), stringStringEntry.getValue());
                }
                gse.run(scriptName, binding);
            } catch (Exception e){
                e.printStackTrace();
            }finally {
                try { if (socket!=null) socket.close(); } catch (IOException ioe) {}
                System.out.println("Connection closed finished");
            }
        }

    }

    private static void printUsage() {
        System.out.println("Usage: java SimpleTelentSimulator [-p <port (default: 23) >] [-d <script_dirs (default: .)>] -f <script_name> ");
    }

    public static Map<String, String> parseCmdParams(String[] args) {
        Map<String, String> result = new HashMap<String, String>();
        String key = null;
        for (String arg : args) {
            if (key == null) {
                key = arg;
            } else {
                result.put(key,arg);
                key = null;
            }
        }
        return result;
    }
}

/*
 * Expect4Groovy.java
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
import net.itransformers.expect4java.cliconnection.CLIConnection;
import net.itransformers.expect4java.cliconnection.CLIStreamLogger;
import net.itransformers.expect4java.cliconnection.utils.OutputStreamCLILogger;
import net.itransformers.expect4java.cliconnection.utils.TeeInputStream;
import net.itransformers.expect4java.cliconnection.utils.TeeOutputStream;
import net.itransformers.expect4groovy.expect4jwrapper.*;
import net.itransformers.expect4java.Expect4j;
import net.itransformers.expect4java.impl.Expect4jImpl;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Expect4Groovy {
    public static void createBindings(CLIConnection cliConnection, Binding binding, boolean withLogging){
        InputStream is = cliConnection.inputStream();
        OutputStream os = cliConnection.outputStream();
        if (withLogging){
            CLIStreamLogger inStreamLogger = new CLIStreamLogger() {
                @Override
                public void log(String message) {
                    System.out.println("<<< "+message);
                }
            };
            CLIStreamLogger outStreamLogger = new CLIStreamLogger() {
                @Override
                public void log(String message) {
                    System.out.println(">>> "+message);
                }
            };
            is = new TeeInputStream(is, new OutputStreamCLILogger(inStreamLogger));
            os = new TeeOutputStream(os, new OutputStreamCLILogger(outStreamLogger));
        }
        Map<String, Object> localBindings = createBindings(new InputStreamReader(is), new OutputStreamWriter(os));
        for (String key : localBindings.keySet()) {
            binding.setProperty(key,localBindings.get(key));
        }
    }

    public static Map<String, Object> createBindings(Reader reader, Writer writer){
        Expect4j expect4j;
        try {
            expect4j = new Expect4jImpl(reader, writer);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create Expect4j.", e);
        }
        Object obj = new Object();
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("expect", new ExpectClosure(obj, expect4j));
        parameters.put("send", new SendClosure(obj, expect4j));
        parameters.put("_re", new RegExpMatchClosure(obj));
        parameters.put("_gl", new GlobMatchClosure(obj));
        parameters.put("_timeout", new TimeoutMatchClosure(obj));
        parameters.put("_eof", new EofMatchClosure(obj));
        parameters.put("reader", reader);
        parameters.put("writer", writer);
        return parameters;
    }
}

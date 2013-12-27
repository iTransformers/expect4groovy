package net.itransformers.expect4groovy;


import expect4j.Expect4j;
import groovy.lang.Binding;
import net.itransformers.expect4groovy.cliconnection.CLIConnection;
import net.itransformers.expect4groovy.cliconnection.utils.OutputStreamCLILogger;
import net.itransformers.expect4groovy.cliconnection.utils.TeeInputStream;
import net.itransformers.expect4groovy.cliconnection.utils.TeeOutputStream;
import net.itransformers.expect4groovy.expect4jwrapper.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class Expect4Groovy {
    public static void createBindings(CLIConnection cliConnection, Binding binding, boolean withLogging){
        InputStream is = cliConnection.inputStream();
        OutputStream os = cliConnection.outputStream();
        if (withLogging){
            is = new TeeInputStream(is, new OutputStreamCLILogger(false));
            os = new TeeOutputStream(os, new OutputStreamCLILogger(true));
        }
        Map<String, Object> localBindings = createBindings(is,os);
        for (String key : localBindings.keySet()) {
            binding.setProperty(key,localBindings.get(key));
        }
    }

    public static Map<String, Object> createBindings(CLIConnection cliConnection){
        return createBindings(cliConnection.inputStream(), cliConnection.outputStream());

    }

    public static Map<String, Object> createBindings(InputStream is, OutputStream os){
        Expect4j expect4j;
        try {
            expect4j = new Expect4j(is, os);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create Expect4j.", e);
        }
        Object obj = new Object();
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("expect", new ExpectClosure(obj, expect4j));
        parameters.put("send", new SendClosure(obj, expect4j));
        parameters.put("_re", new RegExpMatchClosure(obj));
        parameters.put("_gl", new GlobMatchClosure(obj));
        parameters.put("timeout", new TimeoutMatchClosure(obj));
        parameters.put("eof", new EofMatchClosure(obj));
        return parameters;
    }
}

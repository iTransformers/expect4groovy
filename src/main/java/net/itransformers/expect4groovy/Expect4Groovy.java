package net.itransformers.expect4groovy;


import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;
import net.itransformers.expect4java.cliconnection.CLIConnection;
import net.itransformers.expect4java.cliconnection.utils.OutputStreamCLILogger;
import net.itransformers.expect4java.cliconnection.utils.TeeInputStream;
import net.itransformers.expect4java.cliconnection.utils.TeeOutputStream;
import net.itransformers.expect4groovy.expect4jwrapper.*;
import net.itransformers.expect4java.Expect4j;
import net.itransformers.expect4java.impl.Expect4jImpl;
import org.codehaus.groovy.reflection.CachedClass;

import java.io.*;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class Expect4Groovy {

    public static Map<String, Object> createObjects(CLIConnection cliConnection, boolean withLogging, MetaClass clazz){
        final Map<String, Object> expClosures = doCreateObjects(cliConnection, withLogging);
        clazz.getMetaMethods().add(new MetaMethod() {
            @Override
            public int getModifiers() {
                return Modifier.PUBLIC;
            }

            @Override
            public String getName() {
                return "methodMissing";
            }

            @Override
            public Class getReturnType() {
                return Object.class;
            }

            @Override
            public CachedClass getDeclaringClass() {
                return null;
            }

            @Override
            public Object invoke(Object o, Object[] objects) {
                if (objects.length == 0) {
                    throw new IllegalArgumentException("Missing name parameter");
                }
                String name = (String) objects[0];
                Object[] args = new Object[objects.length-1];
                System.arraycopy(objects,1,args,0,args.length);
                Closure closure = (Closure) expClosures.get(name);
                return closure.call(args);
            }
        });
        return expClosures;
    }

    public static Map<String, Object> createObjects(CLIConnection cliConnection, boolean withLogging){
        return doCreateObjects(cliConnection, withLogging);
    }

    public static void createBindings(CLIConnection cliConnection, Binding binding, boolean withLogging){
        Map<String, Object> localBindings = doCreateObjects(cliConnection, withLogging);
        for (String key : localBindings.keySet()) {
            binding.setProperty(key,localBindings.get(key));
        }
    }

    private static Map<String, Object> doCreateObjects(CLIConnection cliConnection, boolean withLogging) {
        InputStream is = cliConnection.inputStream();
        OutputStream os = cliConnection.outputStream();
        if (withLogging){
            is = new TeeInputStream(is, new OutputStreamCLILogger(false));
            os = new TeeOutputStream(os, new OutputStreamCLILogger(true));
        }
        return createObjects(new InputStreamReader(is), new OutputStreamWriter(os));
    }

    public static Map<String, Object> createBindings(Reader reader, Writer writer){
        return createObjects(reader, writer);
    }

    private static Map<String, Object> createObjects(Reader reader, Writer writer){
        Expect4j expect4j;
        try {
            expect4j = new Expect4jImpl(reader, writer);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create Expect4j.", e);
        }
        Object obj = new Object();
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("setTimeout", new SetTimeoutClosure(obj, expect4j));
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

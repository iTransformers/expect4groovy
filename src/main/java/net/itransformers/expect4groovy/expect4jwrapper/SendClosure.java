package net.itransformers.expect4groovy.expect4jwrapper;

import groovy.lang.Closure;
import net.itransformers.expect4java.Expect4j;

import java.io.IOException;

public class SendClosure extends Closure {
    private Expect4j expect4j;

    public SendClosure(Object owner, Object thisObject, Expect4j expect4j) {
        super(owner, thisObject);
        this.expect4j = expect4j;
    }

    public SendClosure(Object owner, Expect4j expect4j) {
        super(owner);
        this.expect4j = expect4j;
    }

    @Override
    public Object call(Object arguments) {
        if (arguments instanceof String) {
            try {
                expect4j.send((String) arguments);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
        return super.call(arguments);
    }
    public Object call(String msg){
        return call((Object)msg);
    }
}

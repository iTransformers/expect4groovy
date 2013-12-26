package net.itransformers.expect4groovy.expect4jwrapper;

import expect4j.ExpectState;
import groovy.lang.Closure;

public class RegExpMatchClosure extends Closure {
    public RegExpMatchClosure(Object owner, Object thisObject) {
        super(owner, thisObject);
    }

    public RegExpMatchClosure(Object owner) {
        super(owner);
    }

    @Override
    public Object call(final Object... args) {
        try {
            if ((args.length == 1) && (args[0] instanceof String)) {
                return new expect4j.matches.RegExpMatch((String)args[0], new expect4j.Closure() {
                    @Override
                    public void run(ExpectState expectState) throws Exception {

                    }
                });
            } else if ((args.length == 2) && (args[0] instanceof String) && (args[1] instanceof Closure)) {
                return new expect4j.matches.RegExpMatch((String)args[0], new expect4j.Closure() {
                    @Override
                    public void run(ExpectState expectState) throws Exception {
                        ((Closure)args[1]).call(expectState);
                    }
                });
            } else {
                return super.call(args);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

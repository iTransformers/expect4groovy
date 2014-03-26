package net.itransformers.expect4groovy.expect4jwrapper;

import net.itransformers.expect4java.Closure;
import net.itransformers.expect4java.ExpectContext;
import net.itransformers.expect4java.matches.GlobMatch;

public class GlobMatchClosure extends groovy.lang.Closure {
    public GlobMatchClosure(Object owner, Object thisObject) {
        super(owner, thisObject);
    }

    public GlobMatchClosure(Object owner) {
        super(owner);
    }

    @Override
    public Object call(final Object... args) {
        try {
            if ((args.length == 1) && (args[0] instanceof String)) {
                return new GlobMatch((String)args[0], new Closure() {
                    @Override
                    public void run(ExpectContext expectState) throws Exception {
                        // Do nothing
                    }
                });
            } else if ((args.length == 2) && (args[0] instanceof String) && (args[1] instanceof groovy.lang.Closure)) {
                return new GlobMatch(args[0].toString(), new Closure() {
                    @Override
                    public void run(ExpectContext expectState) throws Exception {
                        ((groovy.lang.Closure)args[1]).call(expectState);
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

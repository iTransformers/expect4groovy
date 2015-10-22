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

        if ((args.length == 1) && (args[0] instanceof CharSequence)) {
            try {
                return new GlobMatch(args[0].toString(), new Closure() {
                    @Override
                    public void run(ExpectContext expectState) throws Exception {
                        // Do nothing
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ((args.length == 2) && (args[0] instanceof CharSequence) && (args[1] instanceof groovy.lang.Closure)) {
            try {
                return new GlobMatch(args[0].toString(), new Closure() {
                    @Override
                    public void run(ExpectContext expectState) throws Exception {
                        ((groovy.lang.Closure) args[1]).call(expectState);
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("Expected argument of type String");
        }

    }

}

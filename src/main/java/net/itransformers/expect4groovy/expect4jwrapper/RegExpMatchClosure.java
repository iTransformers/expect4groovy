package net.itransformers.expect4groovy.expect4jwrapper;


import net.itransformers.expect4java.Closure;
import net.itransformers.expect4java.ExpectContext;
import net.itransformers.expect4java.matches.RegExpMatch;

public class RegExpMatchClosure extends groovy.lang.Closure {
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
                return new RegExpMatch((String)args[0], new Closure() {
                    @Override
                    public void run(ExpectContext expectState) throws Exception {

                    }
                });
            } else if ((args.length == 2) && (args[0] instanceof String) && (args[1] instanceof groovy.lang.Closure)) {
                return new RegExpMatch((String)args[0], new Closure() {
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

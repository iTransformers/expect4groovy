package net.itransformers.expect4groovy.expect4jwrapper;

import expect4j.ExpectState;
import groovy.lang.Closure;

/**
 * Simulates: expect { timeout { code } }
 */
public class TimeoutMatchClosure extends Closure {
    public TimeoutMatchClosure(Object owner, Object thisObject) {
        super(owner, thisObject);
    }

    public TimeoutMatchClosure(Object owner) {
        super(owner);
    }

    @Override
    public Object call(final Object... args) {
        if ((args.length == 1) && (args[0] instanceof Closure)){
            return new expect4j.matches.TimeoutMatch(new expect4j.Closure() {
                @Override
                public void run(ExpectState expectState) throws Exception {
                    ((Closure)args[0]).call(expectState);
                }
            });
        } else if ((args.length == 2)  && (args[0] instanceof Integer || args[0] instanceof Long )
                && (args[1] instanceof Closure)){
            return new expect4j.matches.TimeoutMatch((Long)args[0], new expect4j.Closure() {
                @Override
                public void run(ExpectState expectState) throws Exception {
                    ((Closure)args[1]).call(expectState);
                }
            });
        } else {
            return super.call(args);
        }
    }

}

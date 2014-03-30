package net.itransformers.expect4groovy.expect4jwrapper;

import net.itransformers.expect4java.Closure;
import net.itransformers.expect4java.ExpectContext;
import net.itransformers.expect4java.matches.TimeoutMatch;

/**
 * Simulates: expect { timeout { code } }
 */
public class TimeoutMatchClosure extends groovy.lang.Closure {
    public TimeoutMatchClosure(Object owner, Object thisObject) {
        super(owner, thisObject);
    }

    public TimeoutMatchClosure(Object owner) {
        super(owner);
    }

    @Override
    public Object call(final Object... args) {
        if ((args.length == 1) && (args[0] instanceof groovy.lang.Closure)){
            return new TimeoutMatch(new Closure() {
                @Override
                public void run(ExpectContext expectState) throws Exception {
                    ((groovy.lang.Closure)args[0]).call(expectState);
                }
            });
        } else if ((args.length == 1)  && (args[0] instanceof Integer || args[0] instanceof Long )) {
            return new TimeoutMatch((Long)args[0]);
        } else if ((args.length == 2)  && (args[0] instanceof Integer || args[0] instanceof Long )
                && (args[1] instanceof groovy.lang.Closure)){
            Long timeout;
            if (args[0] instanceof Integer){
                timeout = Long.valueOf(((Integer)args[0]));
            } else {
                timeout = (Long) args[0];
            }
            return new TimeoutMatch(timeout, new Closure() {
                @Override
                public void run(ExpectContext expectState) throws Exception {
                    ((groovy.lang.Closure)args[1]).call(expectState);
                }
            });
        } else {
            return super.call(args);
        }
    }

}

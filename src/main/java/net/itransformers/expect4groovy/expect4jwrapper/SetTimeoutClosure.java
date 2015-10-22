package net.itransformers.expect4groovy.expect4jwrapper;


import net.itransformers.expect4java.Closure;
import net.itransformers.expect4java.Expect4j;
import net.itransformers.expect4java.ExpectContext;
import net.itransformers.expect4java.matches.GlobMatch;
import net.itransformers.expect4java.matches.Match;
import net.itransformers.expect4java.matches.TimeoutMatch;

import java.util.ArrayList;
import java.util.List;

public class SetTimeoutClosure extends groovy.lang.Closure {

    private Expect4j expect4j;

    public SetTimeoutClosure(Object owner, Expect4j expect4j) {
        super(owner);
        this.expect4j = expect4j;
    }

    @Override
    public Object call(Object... args) {
        if (args.length == 1 && args[0] instanceof TimeoutMatch) {
            setTimeout((TimeoutMatch) args[0]);
            return args[0];
        } else if ((args.length == 2) && (args[0] instanceof Integer) && (args[1] instanceof groovy.lang.Closure)) {
            try {
                final groovy.lang.Closure closure = (groovy.lang.Closure) args[1];
                setTimeout((Integer) args[0], closure);
                return closure;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ((args.length == 2) && (args[0] instanceof Long) && (args[1] instanceof groovy.lang.Closure)){
            try {
                final groovy.lang.Closure closure = (groovy.lang.Closure) args[1];
                setTimeout((Long)args[0], closure);
                return closure;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return super.call(args);
        }
    }

    private void setTimeout(TimeoutMatch timeoutMatch){
        expect4j.setTimeout(timeoutMatch);
    }

    private void setTimeout(Long timeout, final groovy.lang.Closure closure){
        setTimeout(new TimeoutMatch(timeout, new Closure() {
            @Override
            public void run(ExpectContext expectState) throws Exception {
                closure.call(expectState);
            }
        }));
    }

    private void setTimeout(Integer timeout, final groovy.lang.Closure closure){
        setTimeout((long)timeout,closure);
    }
}

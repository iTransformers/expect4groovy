package net.itransformers.expect4groovy.expect4jwrapper;

import net.itransformers.expect4java.Closure;
import net.itransformers.expect4java.ExpectContext;
import net.itransformers.expect4java.matches.EofMatch;

public class EofMatchClosure extends groovy.lang.Closure {


    public EofMatchClosure(Object owner, Object thisObject) {
        super(owner, thisObject);
    }

    public EofMatchClosure(Object owner) {
        super(owner);
    }

    @Override
    public Object call(final Object... args) {
        if ((args.length == 0)){
            return new EofMatch(null);
        } else if ((args.length == 1) && (args[0] instanceof groovy.lang.Closure)){
            return new EofMatch(new Closure() {
                @Override
                public void run(ExpectContext expectState) throws Exception {
                    ((groovy.lang.Closure)args[0]).call(expectState);
                }
            });
        } else {
            return super.call(args);
        }
    }

}

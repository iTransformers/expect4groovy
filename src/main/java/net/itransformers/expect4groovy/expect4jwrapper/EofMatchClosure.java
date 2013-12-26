package net.itransformers.expect4groovy.expect4jwrapper;

import expect4j.ExpectState;
import groovy.lang.Closure;

public class EofMatchClosure extends Closure {


    public EofMatchClosure(Object owner, Object thisObject) {
        super(owner, thisObject);
    }

    public EofMatchClosure(Object owner) {
        super(owner);
    }

    @Override
    public Object call(final Object... args) {
        if ((args.length == 0)){
            return new expect4j.matches.EofMatch();
        } else if ((args.length == 1) && (args[0] instanceof Closure)){
            return new expect4j.matches.EofMatch(new expect4j.Closure() {
                @Override
                public void run(ExpectState expectState) throws Exception {
                    ((Closure)args[0]).call(expectState);
                }
            });
        } else {
            return super.call(args);
        }
    }

}

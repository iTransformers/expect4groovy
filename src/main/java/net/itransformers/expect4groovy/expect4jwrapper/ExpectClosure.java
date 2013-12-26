package net.itransformers.expect4groovy.expect4jwrapper;

import expect4j.Expect4j;
import expect4j.ExpectState;
import expect4j.matches.*;
import expect4j.matches.GlobMatch;
import groovy.lang.Closure;

import java.util.ArrayList;
import java.util.List;

public class ExpectClosure extends Closure {

    private Expect4j expect4j;
    private expect4j.matches.TimeoutMatch defaultTimeoutMatch;

    public ExpectClosure(Object owner, Object thisObject, Expect4j expect4j) {
        super(owner, thisObject);
        this.expect4j = expect4j;
    }

    public ExpectClosure(Object owner, Expect4j expect4j) {
        super(owner);
        this.expect4j = expect4j;
    }


    public Object call(final ArrayList arrayList) {
        return call((Object)arrayList);
    }


    @Override
    public Object call(Object... args) {
        if ((args.length == 1) && (args[0] instanceof String)) {
            try {
                return expect4j.expect((String) args[0]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ((args.length == 1) && (args[0] instanceof Match)) {
            try {
                return expect4j.expect(new Match[]{(Match) args[0]});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ((args.length == 2) && (args[0] instanceof String) && (args[1] instanceof Closure)){
            try {
                final Closure closure = (Closure) args[1];
                return expect4j.expect((String) args[0], new expect4j.Closure() {
                    @Override
                    public void run(ExpectState expectState) throws Exception {
                        closure.call(expectState);
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ((args.length == 1) && args[0] instanceof List){
            List list = (List) args[0];
            List<expect4j.matches.Match> matchesList = new ArrayList<expect4j.matches.Match>(list.size()+1);
            boolean hasTimeoutMatch = false;
            for (Object element : list) {
                if (element instanceof String) {
                    try {
                        matchesList.add(new GlobMatch((String) element, new expect4j.Closure() {
                            @Override
                            public void run(ExpectState expectState) throws Exception {
                                // Do nothing
                            }
                        }));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else if (element instanceof Match) {
                    matchesList.add((Match) element);
                    if (element instanceof TimeoutMatch) {
                        hasTimeoutMatch = true;
                    }
                } else {
                    throw new RuntimeException("Element of type: " + element.getClass() + "is not expected");
                }
            }
            if (!hasTimeoutMatch && defaultTimeoutMatch != null){
                matchesList.add(defaultTimeoutMatch);
            }
            try {
                return expect4j.expect(matchesList);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return super.call(args);
        }
    }

    public void setDefaultTimeoutMatch(Object defaultTimeoutMatch){
        this.defaultTimeoutMatch = (expect4j.matches.TimeoutMatch)defaultTimeoutMatch;
    }
}

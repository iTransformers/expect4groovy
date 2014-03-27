package net.itransformers.expect4groovy.expect4jwrapper;


import net.itransformers.expect4java.Closure;
import net.itransformers.expect4java.Expect4j;
import net.itransformers.expect4java.ExpectContext;
import net.itransformers.expect4java.matches.GlobMatch;
import net.itransformers.expect4java.matches.Match;
import net.itransformers.expect4java.matches.TimeoutMatch;

import java.util.ArrayList;
import java.util.List;

public class ExpectClosure extends groovy.lang.Closure {

    private Expect4j expect4j;
    private TimeoutMatch defaultTimeoutMatch;

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
                return expect4j.expect(new Match[]{new GlobMatch((String) args[0], null)});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ((args.length == 1) && (args[0] instanceof Match)) {
            try {
                return expect4j.expect(new Match[]{(Match) args[0]});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ((args.length == 2) && (args[0] instanceof String) && (args[1] instanceof groovy.lang.Closure)){
            try {
                final groovy.lang.Closure closure = (groovy.lang.Closure) args[1];
                return expect4j.expect(new Match[]{new GlobMatch((String) args[0], new Closure() {
                    @Override
                    public void run(ExpectContext expectState) throws Exception {
                        closure.call(expectState);
                    }
                })});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ((args.length == 1) && args[0] instanceof List){
            List list = (List) args[0];
            List<Match> matchesList = new ArrayList<Match>(list.size()+1);
            boolean hasTimeoutMatch = false;
            for (Object element : list) {
                if (element instanceof String) {
                    try {
                        matchesList.add(new GlobMatch((String) element, new Closure() {
                            @Override
                            public void run(ExpectContext expectState) throws Exception {
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
                return expect4j.expect(matchesList.toArray(new Match[matchesList.size()]));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return super.call(args);
        }
    }

    public void setDefaultTimeoutMatch(Object defaultTimeoutMatch){
        this.defaultTimeoutMatch = (TimeoutMatch)defaultTimeoutMatch;
    }
}
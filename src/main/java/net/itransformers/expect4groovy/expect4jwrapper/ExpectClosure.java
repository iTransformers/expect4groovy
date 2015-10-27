package net.itransformers.expect4groovy.expect4jwrapper;


import net.itransformers.expect4java.Closure;
import net.itransformers.expect4java.Expect4j;
import net.itransformers.expect4java.ExpectContext;
import net.itransformers.expect4java.matches.GlobMatch;
import net.itransformers.expect4java.matches.Match;

import java.util.ArrayList;
import java.util.List;

public class ExpectClosure extends groovy.lang.Closure {

    private Expect4j expect4j;

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
        if ((args.length == 1) && (args[0] instanceof CharSequence)) {
            try {
                return expect4j.expect(new Match[]{new GlobMatch(args[0].toString(), null)});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ((args.length == 1) && (args[0] instanceof Match)) {
            try {
                return expect4j.expect(new Match[]{(Match) args[0]});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ((args.length == 2) && (args[0] instanceof CharSequence) && (args[1] instanceof groovy.lang.Closure)){
            try {
                final groovy.lang.Closure closure = (groovy.lang.Closure) args[1];
                return expect4j.expect(new Match[]{new GlobMatch(args[0].toString(), new Closure() {
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
            for (Object element : list) {
                if (element instanceof CharSequence) {
                    try {
                        matchesList.add(new GlobMatch(element.toString(), new Closure() {
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
                } else {
                    throw new RuntimeException("Element of type: " + element.getClass() + "is not expected");
                }
            }
            try {
                return expect4j.expect(matchesList.toArray(new Match[matchesList.size()]));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("Expected argument of type String");
        }
    }

    public void close(){
        expect4j.close();
    }

}

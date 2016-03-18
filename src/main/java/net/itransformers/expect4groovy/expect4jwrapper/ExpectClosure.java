/*
 * ExpectClosure.java
 *
 * Copyright 2016  iTransformers Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.itransformers.expect4groovy.expect4jwrapper;


import net.itransformers.expect4java.Closure;
import net.itransformers.expect4java.Expect4j;
import net.itransformers.expect4java.ExpectContext;
import net.itransformers.expect4java.matches.GlobMatch;
import net.itransformers.expect4java.matches.Match;
import net.itransformers.expect4java.matches.TimeoutMatch;

import java.io.IOException;
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

    public void setTimeout(TimeoutMatch timeoutMatch){
        expect4j.setTimeout(timeoutMatch);
    }
    public void setTimeout(long timeout, final groovy.lang.Closure closure){
        setTimeout(new TimeoutMatch((long) timeout, new Closure() {
            @Override
            public void run(ExpectContext expectState) throws Exception {
                closure.call(expectState);
            }
        }));
    }
    public void setTimeout(int timeout, final groovy.lang.Closure closure){
        setTimeout((long)timeout,closure);
    }

    public void close() throws IOException {
        expect4j.close();
    }

}

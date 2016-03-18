/*
 * GlobMatchClosure.java
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
import net.itransformers.expect4java.ExpectContext;
import net.itransformers.expect4java.matches.GlobMatch;

public class GlobMatchClosure extends groovy.lang.Closure {
    public GlobMatchClosure(Object owner, Object thisObject) {
        super(owner, thisObject);
    }

    public GlobMatchClosure(Object owner) {
        super(owner);
    }

    @Override
    public Object call(final Object... args) {

        if ((args.length == 1) && (args[0] instanceof CharSequence)) {
            try {
                return new GlobMatch(args[0].toString(), new Closure() {
                    @Override
                    public void run(ExpectContext expectState) throws Exception {
                        // Do nothing
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if ((args.length == 2) && (args[0] instanceof CharSequence) && (args[1] instanceof groovy.lang.Closure)) {
            try {
                return new GlobMatch(args[0].toString(), new Closure() {
                    @Override
                    public void run(ExpectContext expectState) throws Exception {
                        ((groovy.lang.Closure) args[1]).call(expectState);
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("Expected argument of type String");
        }

    }

}

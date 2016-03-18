/*
 * EofMatchClosure.java
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
            throw new IllegalArgumentException("Expected argument of type String");
        }
    }

}

/*
 * SendClosure.java
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

import groovy.lang.Closure;
import net.itransformers.expect4java.Expect4j;

import java.io.IOException;

public class SendClosure extends Closure {
    private Expect4j expect4j;

    public SendClosure(Object owner, Object thisObject, Expect4j expect4j) {
        super(owner, thisObject);
        this.expect4j = expect4j;
    }

    public SendClosure(Object owner, Expect4j expect4j) {
        super(owner);
        this.expect4j = expect4j;
    }

    @Override
    public Object call(Object... arguments) {
        return this.call(arguments[0]);
    }

    @Override
    public Object call(Object arguments) {
        if (arguments instanceof CharSequence) {
            try {
                expect4j.send(arguments.toString());
                return null;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalArgumentException("Expected argument of type String");
    }
    public Object call(String msg){
        return call((Object)msg);
    }
}

/*
 * test_regexp_nested_states.groovy
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

package net.itransformers.expect4groovy

import net.itransformers.expect4java.ExpectContext

boolean match1 = false
boolean match2 = false
boolean match3 = false
send "hello1 World\n"
send "hello2 World\n"
send "hello3 World\n"
expect([
    _re("hello1 ([^\n]*)\n") {  ExpectContext context ->
        println ("Hello1 " + context.getMatch(1))
        match1 = true
        expect ([_re("hello2 ([^\n]*)\n") {  ExpectContext context1 ->
                println ("Hello2 " + context1.getMatch(1))
                match2 = true
            }])
    },
    _re("hello3 ([^\n]*)\n") {  ExpectContext context ->
        println ("Hello3 " + context.getMatch(1))
        match3 = true
    },
])

return match1 && match2 && !match3
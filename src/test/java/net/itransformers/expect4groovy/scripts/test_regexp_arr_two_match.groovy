/*
 * test_regexp_arr_two_match.groovy
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

package net.itransformers.expect4groovy.scripts

import net.itransformers.expect4java.ExpectContext

boolean status = false
boolean firsMatch = false
send "hello World\n"
send "hello2 World\n"
expect([
    _re("hello ([^\n]*)\n") {  ExpectContext context ->
        println ("Hello " + context.getMatch(1))
        firsMatch = true
        context.exp_continue()
    },
    _re("hello2 ([^\n]*)\n") {  ExpectContext context ->
        println ("Hello2 " + context.getMatch(1))
        if (firsMatch) status = true
    }
])

return status
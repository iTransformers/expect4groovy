/*
 * telnet_simulator.groovy
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

def prompt = "# "
send("user>")
expect([
        _re ("([^\r]*)\r")
])

send("password>")
expect( [
        _re ("([^\r]*)\r") {
            send (prompt)
        }
])

expect([
        _re ("\nsay hello\r") {
            println "match regexp say hello"
            send ("hello\r")
            send ("hello 123\r")
            send (prompt)
            it.exp_continue()
        },
        _re ("\nexit\r") {
            println "received exist"
        },
        _re ("([^\r]*)\r") {
            println "match regexp"
            send (prompt)
            it.exp_continue()
        },
        timeout {
            println "Timeouted"
        }

])


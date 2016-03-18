/*
 * test.groovy
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

import net.itransformers.expect4groovy.Expect4Groovy
import net.itransformers.expect4java.cliconnection.CLIConnection
import net.itransformers.expect4java.cliconnection.impl.SshCLIConnection

CLIConnection conn = new SshCLIConnection()
def params = ["username": "vvv", "password": "", "address": "localhost", "port":"22"]

conn.connect(params)

Expect4Groovy.createBindings(conn, getBinding(), true)
expect.setTimeout(1000){
    println "Timeouted"
}

expect ("\$") {
    send ("exit\n")
}
expect _eof() {
    println "eof found"
}

conn.disconnect()
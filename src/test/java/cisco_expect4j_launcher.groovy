/*
 * cisco_expect4j_launcher.groovy
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
import net.itransformers.expect4java.cliconnection.impl.TelnetCLIConnection
import net.itransformers.expect4java.cliconnection.impl.RawSocketCLIConnection
import net.itransformers.expect4java.cliconnection.impl.SshCLIConnection

params = ["protocol":"ssh", "username": "lab", "password": "lab123", "enablePass": "pass123", "address": "172.16.35.5","port":"22", "command": "no ip domain-lookup"]

CLIConnection conn = null
if (params["protocol"]=="telnet"){
     conn = new TelnetCLIConnection()
} else if (params["protocol"]=="raw"){
     conn = new RawSocketCLIConnection()
}  else {
     conn = new SshCLIConnection()
}

conn.connect(params)

Expect4Groovy.createBindings(conn, getBinding(), true)

evaluate(new File("cisco_expect4j.groovy"))

conn.disconnect()
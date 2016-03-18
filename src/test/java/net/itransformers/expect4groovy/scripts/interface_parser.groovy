/*
 * interface_parser.groovy
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
boolean status = false;

String cmdResponse = new File("src/test/java/net/itransformers/expect4groovy/scripts","interface_command.txt").text;
send(cmdResponse)

expect.setTimeout(_timeout(60000){
    status = false
})

def interfaces = [:]
expect([
        _re("interface ([^\n]+)\n"){ ExpectContext context ->
            def currentInterfaceName = context.getMatch(1)
            def listOfIpOpts = []
            interfaces[currentInterfaceName] = listOfIpOpts
            println ("1>"+context.buffer)
            expect([
                    _re(" no ip redirects([^\n]+)\n"){ it1 ->
                        listOfIpOpts.add(it1.getMatch(1))
                        println ("ip redirects disabled!"+it1.buffer)
                        it1.exp_continue()
                    },
                    _re(" no ip unreachables([^\n]+)\n"){ it1 ->
                        listOfIpOpts.add(it1.getMatch(1))
                        println ("ICMP unreachable messages disabled!"+it1.buffer)
                        it1.exp_continue()
                    },
                    _re(" no ip unreachables([^\n]+)\n"){ it1 ->
                        listOfIpOpts.add(it1.getMatch(1))
                        println ("Proxy ARP messages disabled!"+it1.buffer)
                        it1.exp_continue()
                    },

                    _re("shutdown\n"){ it1 ->
                        println ("Interface Administratively down> "+it1.buffer)
                        it1.exp_continue()
                    },
                    _re("[^\n!]+\n"){ it1 ->
                        println ("4>"+it1.buffer)
                        it1.exp_continue()
                    },
                    _re("[!]\n"){  it1 ->
                        println ("5>"+it1.buffer)
                        println "exit inner closure"
                        // do nothing, just exit expect closure
                    }
            ])
            println ("6>"+context.buffer)
            context.exp_continue()
        },
        _re("router ([^\n]+)\n"){
            println ("7>"+it.buffer)
            expect([
                _re("[^\n!]+\n"){ it1->
                    println ("8"+it1.buffer)
                    it1.exp_continue()
                },
                _re("[!]\n"){ it1 ->
                    println ("9>"+it1.buffer)
                    // do nothing, just exit expect closure
                }
            ])
            println ("10>"+it.buffer)
            it.exp_continue()
        },
        _re("[^\n#]+\n"){  ExpectContext context2  ->
            println ("11>"+context2.buffer)
            expect([
                    _re("[^\n!]+\n"){ it1->
                        println ("12>"+it1.buffer)
                        it1.exp_continue()
                    },
                    _re("[!]\n"){ it1 ->
                        println ("13>"+it1.buffer)
                        println "exit inner clousre for not tracked part of config"
                        // do nothing, just exit expect closure
                    }
            ])
            println ("14>"+context2.buffer)
            context2.exp_continue()
        },
        _re("#\n"){
            println ("15>"+it.buffer)
            println "exit outer closure"
            // do nothing, just exit the outer closure
        },
        _re(".*"){
            println ("16"+it.buffer)
        }
])

//def result = [:]
//result["interfaces"]=interfaces
println interfaces
return true
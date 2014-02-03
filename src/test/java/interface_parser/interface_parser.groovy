package interface_parser
String cmdResponse = new File("src/test/java/interface_parser/","interface_command.txt").text;
send(cmdResponse)
expect.setDefaultTimeoutMatch(timeout(60000l){
    println "timeouted"
})
def interfaces = [:]
expect([
        _re("interface ([^\n]+)\n"){ expect4j.ExpectState state ->
            def currentInterfaceName = state.getMatch(1)
            def listOfIpOpts = []
            interfaces[currentInterfaceName] = listOfIpOpts
            println ("1>"+state.buffer)
            expect([
                    _re(" no ip([^\n]+)\n"){ it1 ->
                        listOfIpOpts.add(it1.getMatch(1))
                        println ("2>"+t1.buffer)
                        it1.exp_continue()
                    },
                    _re("shutdown\n"){ it1 ->
                        println ("3">+ait1.buffer)
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
            println ("6>"+state.buffer)
            state.exp_continue()
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
            println ("10>"+it1.buffer)
            it.exp_continue()
        },
        _re("[^\n#]+\n"){  expect4j.ExpectState state2  ->
            println ("11>"+state2.buffer)
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
            println ("14>"+state2.buffer)
            state2.exp_continue()
        },
        _re("#\n"){
            println ("15>"+it1.buffer)
            println "exit outer closure"
            // do nothing, just exit the outer closure
        },
        _re(".*"){
            println ("16"+it.buffer)
        }
])

def result = [:]
result["interfaces"]=interfaces
return result
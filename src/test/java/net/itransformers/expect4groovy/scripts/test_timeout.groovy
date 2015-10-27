package net.itransformers.expect4groovy.scripts

boolean status = false
send "hello\n"
expect([
        _gl("hello\n") {
            println "Hello World!"
            Thread.sleep(1100)
            it.exp_continue()
        },
        _timeout(1000){
            status = true
        }
])
return status
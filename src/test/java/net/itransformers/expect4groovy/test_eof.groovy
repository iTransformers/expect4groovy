package net.itransformers.expect4groovy

boolean status = false
send "hello\n"
expect([
        _gl("hello\n") {
            println "Hello World!"
            writer.close();
            println "reader closed"
            it.exp_continue()
        },
        _eof {
            status = true
        }
])
return status
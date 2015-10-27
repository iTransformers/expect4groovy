package net.itransformers.expect4groovy.scripts

boolean status = false

setTimeout(_timeout(1000){
    status = true
})

send "hello\n"
expect([
        _gl("hello\n") {
            println "Hello World!"
            Thread.sleep(1100)
            it.exp_continue()
        }

])
return status
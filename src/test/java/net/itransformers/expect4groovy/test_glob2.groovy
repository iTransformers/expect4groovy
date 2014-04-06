package net.itransformers.expect4groovy

boolean status = false
send "hello\n"

// this is equivalent of: expect _gl("hello\n") {}
expect ("hello\n") {
    println "Hello World!"
    status = true
}
return status
package net.itransformers.expect4groovy.scripts

boolean status = false
send "hello\n"

// this is equivalent of: expect _gl("hello\n") {},
// but uses two arguments for expect closure
expect ("hello\n") {
    println "Hello World!"
    status = true
}
return status
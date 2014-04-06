package net.itransformers.expect4groovy

boolean status = false
send "hello\n"
expect _gl("hello\n") {
    println "Hello World!"
    status = true
}
return status
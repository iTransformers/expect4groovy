package net.itransformers.expect4groovy.scripts

import net.itransformers.expect4java.ExpectContext

boolean status = false
send "hello World\n"
expect _re("hello ([^\n]*)\n") {  ExpectContext context ->
    println ("Hello " + context.getMatch(1))
    status = true
}

return status
package net.itransformers.expect4groovy

import net.itransformers.expect4java.ExpectContext

boolean status = false
boolean firsMatch = false
send "hello World\n"
send "hello2 World\n"
expect([
    _re("hello ([^\n]*)\n") {  ExpectContext context ->
        println ("Hello " + context.getMatch(1))
        firsMatch = true
        context.exp_continue()
    },
    _re("hello2 ([^\n]*)\n") {  ExpectContext context ->
        println ("Hello2 " + context.getMatch(1))
        if (firsMatch) status = true
    }
])

return status
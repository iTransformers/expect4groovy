package net.itransformers.expect4groovy.scripts

import net.itransformers.expect4java.ExpectContext

send "hello2 World\n"
boolean status = false;
boolean hasSecondMatched = false;
expect([
    _re("hello ([^\n]*)\n") {  ExpectContext context ->
        println ("Hello " + context.getMatch(1))
        if (!hasSecondMatched) {
            status = false;
        }
        status = true
    },
    _re("hello2 ([^\n]*)\n") {  ExpectContext context ->
        println ("Hello2 " + context.getMatch(1))
        hasSecondMatched = true
        send "hello World\n"
        context.exp_continue()
    },
])

return status
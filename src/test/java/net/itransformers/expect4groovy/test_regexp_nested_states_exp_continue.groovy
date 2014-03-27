package net.itransformers.expect4groovy

import net.itransformers.expect4java.ExpectContext

boolean match1 = false
boolean match2 = false
boolean match3 = false
send "hello1 World\n"
send "hello2 World\n"
send "hello3 World\n"
expect([
    _re("hello1 ([^\n]*)\n") {  ExpectContext context ->
        println ("Hello1 " + context.getMatch(1))
        match1 = true
        expect ([_re("hello2 ([^\n]*)\n") {  ExpectContext context1 ->
                println ("Hello2 " + context1.getMatch(1))
                match2 = true
            }])
        context.exp_continue()
    },
    _re("hello3 ([^\n]*)\n") {  ExpectContext context ->
        println ("Hello3 " + context.getMatch(1))
        match3 = true
    },
])

return match1 && match2 && match3
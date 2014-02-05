def prompt = "# "
send("user>")
expect([
        _re ("([^\r]*)\r")
])

send("password>")
expect( [
        _re ("([^\r]*)\r") {
            send (prompt)
        }
])

expect([
        _re ("\nsay hello\r") {
            println "match regexp say hello"
            send ("hello\r")
            send ("hello 123\r")
            send (prompt)
            it.exp_continue()
        },
        _re ("\nexit\r") {
            println "received exist"
        },
        _re ("([^\r]*)\r") {
            println "match regexp"
            send (prompt)
            it.exp_continue()
        },
        timeout {
            println "Timeouted"
        }

])


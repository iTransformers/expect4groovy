def prompt = "#"

expect "user>"
send "vvv\r"

expect "password>"
send "123\r"
expect prompt

send "some command\r"
expect prompt

send "say hello\r"
expect ([
 _gl("hello\r"){
     println("Matched hello")
     it.exp_continue()
 },
 _re("hello [0-9]+\r"){ expect4j.ExpectState expectState ->
     println("Matched: "+expectState.getMatch())
     expectState.exp_continue()
 },
 prompt
])


send "exit\r"
expect eof()

return ["status": "SUCCESS","data": "Hello"]
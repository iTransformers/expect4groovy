import net.itransformers.expect4groovy.Expect4Groovy
import net.itransformers.expect4groovy.cliconnection.CLIConnection
import net.itransformers.expect4groovy.cliconnection.impl.RawSocketCLIConnection

CLIConnection conn = new RawSocketCLIConnection()
conn.connect(["user":"vasko","password":"123","address":"localhost:23"])

Expect4Groovy.createBindings(conn, getBinding(), true)

def prompt = "#"

expect "user>"
send "vasko\r"

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

conn.disconnect()
import net.itransformers.expect4groovy.Expect4Groovy
import net.itransformers.expect4groovy.cliconnection.CLIConnection
import net.itransformers.expect4groovy.cliconnection.impl.SshCLIConnection

CLIConnection conn = new SshCLIConnection()
def params = ["username": "vvv", "password": "", "address": "localhost", "port":"22"]

conn.connect(params)

Expect4Groovy.createBindings(conn, getBinding(), true)
expect.setTimeout(1000){
    println "Timeouted"
}

expect ("\$") {
    send ("exit\n")
}
expect _eof() {
    println "eof found"
}

conn.disconnect()
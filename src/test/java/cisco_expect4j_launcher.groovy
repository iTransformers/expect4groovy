import net.itransformers.expect4groovy.Expect4Groovy
import net.itransformers.expect4groovy.cliconnection.CLIConnection
import net.itransformers.expect4groovy.cliconnection.impl.TelnetCLIConnection
import net.itransformers.expect4groovy.cliconnection.impl.RawSocketCLIConnection
import net.itransformers.expect4groovy.cliconnection.impl.SshCLIConnection

params = ["protocol":"telnet", "username": "test", "password": "test321", "enablePass": "test321", "address": "87.247.249.134","port":"23", "command": "no ip domain-lookup"]

CLIConnection conn = null
if (params["protocol"]=="telnet"){
     conn = new TelnetCLIConnection()
} else if (params["protocol"]=="raw"){
     conn = new RawSocketCLIConnection()
}  else {
     conn = new SshCLIConnection()
}

conn.connect(params)

Expect4Groovy.createBindings(conn, getBinding(), true)

evaluate(new File("cisco_expect4j.groovy"))

conn.disconnect()
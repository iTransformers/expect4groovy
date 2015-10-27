import net.itransformers.expect4groovy.Expect4Groovy
import net.itransformers.expect4java.cliconnection.CLIConnection
import net.itransformers.expect4java.cliconnection.impl.TelnetCLIConnection
import net.itransformers.expect4java.cliconnection.impl.RawSocketCLIConnection
import net.itransformers.expect4java.cliconnection.impl.SshCLIConnection

params = ["protocol":"ssh", "username": "lab", "password": "lab123", "enablePass": "pass123", "address": "172.16.35.5","port":"22", "command": "no ip domain-lookup"]

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
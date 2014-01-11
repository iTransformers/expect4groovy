import net.itransformers.expect4groovy.Expect4Groovy
import net.itransformers.expect4groovy.cliconnection.CLIConnection
import net.itransformers.expect4groovy.cliconnection.impl.RawSocketCLIConnection

CLIConnection conn = new RawSocketCLIConnection()
def params = ["user": "vasko", "password": "123", "address": "localhost:22223"]

conn.connect(params)

conn.connect(params)

Expect4Groovy.createBindings(conn, getBinding(), true)

evaluate(new File("expect4groovy_test.groovy"))

conn.disconnect()
import net.itransformers.expect4groovy.Expect4Groovy
import net.itransformers.expect4groovy.cliconnection.CLIConnection
import net.itransformers.expect4groovy.cliconnection.impl.EchoCLIConnection
import net.itransformers.expect4java.ExpectContext
import org.apache.log4j.Level
import org.apache.log4j.LogManager

if (args.length == 0){
    LogManager.getRootLogger().setLevel(Level.INFO);
} else {
    LogManager.getRootLogger().setLevel(Level.toLevel(args[0]));
}


CLIConnection conn = new EchoCLIConnection()
def params = [:] // empty map for echo connection
conn.connect(params)

Expect4Groovy.createBindings(conn, getBinding(), true)

expect.setTimeout(1000){
    println "Timeout while expecting"
}

// simple send to echo connection
send("echo\n")
send("test\n")
expect ("echo\n") {
    // Example how to use nested expect closures
    expect("test\n") {
        send("hello\n") // send to echo connection again
    }
}
// expect hello as it should be send already to the echo connection
expect("hello\n");

// Example usage of '_re' closure
send ("echo1234")
expect (_re("[a-z]+([0-9]+)"){
    println("Captured: "+it.getMatch(1))
})

// More complicated examples with array of Match closures.
// Shows also how to use exp_continue of ExpectContext
send ("echo1234\n")
send ("5678echo\n")
send ("ZZZ\n")
expect ([
    _re("[a-z]+([0-9]+)"){ ExpectContext it ->
        println("Captured: "+it.getMatch(1))
        it.exp_continue();
    },
    _re("([0-9]+)[a-z]+"){
        println("Captured: "+it.getMatch())
        it.exp_continue();
    },
    _gl("ZZZ"){
        println("Captured: ZZZ")
    }
])

//Shows how the global timeout closure will be invoked.
send("hello\n")
expect("John"){
    println("This text should not appear in console")
}

//Shows how the local timeout closure will be invoked.
send("test\n")
expect([
    _gl("Smith") {
        println("This text should not appear in console")
    },
    _timeout(500){
        println("This is a timeout example")
    }
])

// Lets close echo connection
conn.disconnect()

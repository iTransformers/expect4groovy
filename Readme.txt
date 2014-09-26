This is the first version of the expect4groovy library.

Dependencies
==================================================
expect4groovy runtime depends on the following other libraries.

- Maven: org.codehaus.groovy:groovy-all:2.1.9
- Maven: org.apache.commons:commons-net:3.3.1
- Maven: com.jcraft:jsch:0.1.44-1 (optional for ssh connections)
- Maven: commons-net:commons-net:3.0.1
- Maven: oro:oro:2.0.8
- Maven: log4j:log4j:1.2.16

Usage
=================================================
Typical usage of this library is inside a groovy script where one can register
the following groovy closures into script bindings:

- send    : SendClosure
  The send closure is used to send characters
  Example of invoking this closure is:
  send("say hello\r")

- expect  : ExpectClosure
  The expect closure has several overloads:
  -- expect(string)
     Example of invoking this closure overload is:
     expect("login:")
  -- expect(string, closure)
     Example of invoking this closure overload is:
     expect("login:") {
       // the closure code invoked if there is a match
     }
  -- expect(Match[] mathes)
     Example of invoking this closure overload is:
     expect ([
      _gl("hello\r"){
          println("Matched hello")
          it.exp_continue()
      },
      _re("hello [0-9]+\r"){ net.itransformers.expect4java.ExpectContext expectState ->
          println("Matched: "+expectState.getMatch())
          expectState.exp_continue()
      }
     ])

- _re     : RegExpMatchClosure
  Match closure object used for matching characters received into input stream using regular expression pattern.
  This Match closure can be used as an array element of parameter of expect closure.
  The _re closure has two overloads:
  -- _re(string_re_pattern)
  -- _re(string_re_pattern,closure)
  The second one has a closure parameter which will be invoked if the regexp matches.

- _gl     : GlobMatchClosure
  Match closure object used for matching characters received into input stream using glob pattern.
  This Match closure can be used as an array element of parameter of expect closure.
  The _re closure has two overloads:
  -- _gl(string)
  -- _gl(string,closure)
  The second one has a closure parameter which will be invoked if the regexp matches.

- timeout : TimeoutMatchClosure
  Match closure object used to handle expect timeouts.
  This Match closure can be used as an array element of parameter of expect closure.
  For example:
  expect ([
     timeout(){
      // some code is executed here if there is timeout
     }
  ])
  or:
  expect ([
     timeout(1000L)
  ])

- eof     : EofMatchClosure

Inside each match closure the following object is available: net.itransformers.expect4java.ExpectContext.
This object has the following most important methods:
 - void exp_continue();
 - void exp_continue_reset_timer();
 - String getBuffer();
 - String getMatch(int groupnum);
 - String getMatch();

Registering groovy closures
====================================================
The above Groovy closures are registered into script bindings with one of the following overloads
of createBindings method:

void Expect4Groovy.createBindings(CLIConnection cliConnection, Binding binding, boolean withLogging);
Map<String, Object> Expect4Groovy.createBindings(CLIConnection cliConnection);
Map<String, Object> Expect4Groovy.createBindings(InputStream is, OutputStream os);

Example:
CLIConnection conn = new RawSocketCLIConnection()
conn.connect(["user":"v","password":"123","address":"localhost:23"])
Expect4Groovy.createBindings(conn, getBinding(), true)

Example expect4groovy script
==================================================
import net.itransformers.expect4java.ExpectContext

def params = ["protocol": "echo"]
String[] roots = new String[1];
roots[0] = path
Object result = new Expect4GroovyScriptLauncher().launch(roots, script, params);


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

Running example
===================================================
1. Running the simulator
export CLASSPATH=expect4j-1.0.jar:groovy-all-2.1.9.jar:oro-2.0.8.jar:expect4groovy-1.0-SNAPSHOT.jar
java simulator.TelnetSimulator -f telnet_simulator.groovy

2. Running the expect4grovy example
export CLASSPATH=expect4j-1.0.jar:expect4groovy-1.0-SNAPSHOT.jar
groovy expect4groovy_test.groovy


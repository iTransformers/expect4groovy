import net.itransformers.expect4groovy.Expect4Groovy
import net.itransformers.expect4groovy.cliconnection.CLIConnection
import net.itransformers.expect4groovy.cliconnection.impl.TelnetCLIConnection
import net.itransformers.expect4groovy.cliconnection.impl.RawSocketCLIConnection
import net.itransformers.expect4groovy.cliconnection.impl.SshCLIConnection

params = ["protocol":"telnet", "username": "misho", "password": "misho321", "enablePass": "misho321", "address": "87.247.249.134","port":"23", "command": "no ip domain-lookup"]

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

prompt = ">"
powerUserPrompt = "#"
defaultTerminator = "\r"
logedIn = "false"
logedInPowerMode = "false"
logedInConfigMode = "false"
hostname = ""
status = ["success": 1, "failure": 2]

println ("Login: " + login())
println ("Setup terminal: " + setupTerminal())

println ("Send Verification command: " + sendCommand())

def login(){
    if (params["protocol"]!="ssh"){
        if (user() == status["success"]) {
           if (password() == status["success"]) {
               if (showPrivilege() == status["success"]){
                   println("Login Successfull")
                   return status["success"]
               }else{
                   return status["failure"]
               }

           }  else {
               return status["failure"]

           }
        }else {
            return status["failure"]
        }
    }
}

def user() {
    def returnFlag = 2;

    expect _re("Username:|User:") {
        send params["username"] + defaultTerminator
        returnFlag = status["success"]

    }
    if(returnFlag != status["success"]){
        println "Invalid Username!"
        return status["failure"]
    }  else {
        return status["success"]
    }

}

def password() {
    def returnFlag = 2;

    expect("Password:") {
        send params["password"] + defaultTerminator
        expect _re("(" + prompt + "\$)" + "|" + "(" + powerUserPrompt + "\$" + ")") {
            println("Password submitted successfully")
            returnFlag = status["success"]
            println("Password submitted successfully: "+returnFlag)
        }
    }
    println("Password submitted successfully2: "+returnFlag)

    return  returnFlag;
}

def showPrivilege() {
    def returnFlag = 2
    send "show privilege"+defaultTerminator
    expect("Current privilege level is 15") {
        returnFlag = status["success"]
    }
    if(returnFlag != status["success"]){
        enablePassword()
    }

    return  returnFlag
}

def enablePassword() {
    def returnFlag = 2
    send "enable"
    expect("Password:") {
        send params["enablePass"] + defaultTerminator
        expect _re(powerUserPrompt + "\$") {
            logedInPowerMode = "true"
            returnFlag = status["success"]
        }
    }
    return returnFlag

}

def setupTerminal() {
    def returnFlag = 2

    expect _re("(" + prompt + "\$)" + "|" + "(" + powerUserPrompt + "\$" + ")") {
        send "terminal length 0" + defaultTerminator

        def match = it.getMatch()
        if (match == prompt) {
            logedIn = "true"
            hostname = it.getBuffer().split(prompt)[0]
            // println ("Hostname: " + hostname)
            returnFlag = status["success"]

        } else if (match == powerUserPrompt) {
            logedIn = "true"
            hostname = it.getBuffer().split(powerUserPrompt)[0]
//            println("Hostname: "+hostname)
            logedInPowerMode = "true"
            returnFlag = status["success"]
        }
        else {
            println "Invalid Prompt Detected!"
            returnFlag = status["failure"]
        }
    }
    return returnFlag
}

def sendCommand() {
//Send verification command
    def returnFlag = 2
    def result = null
        expect _re(hostname + powerUserPrompt + "\$") {
            //    send "show run" + "\r"
            send "show ip int brief" + defaultTerminator
        }
        expect _re(powerUserPrompt + "\$") {
            returnFlag =  status["success"]
            result =  it.getBuffer()
        }

    return ["status": returnFlag,"data": result]


}

//Log in configuration mode
def sendConfigCommand() {

    if (logedInPowerMode == "true") {
        send "configure terminal" + defaultTerminator
        expect _re(powerUserPrompt + "\$") {
            logedInConfigMode = "true"
        }


    } else {
        println("Error Not logged in Power User Mode!")
    }

//Send Configuration command
    if (logedInConfigMode == "true") {
        send params["command"] + defaultTerminator
        expect _re("% Invalid input detected") {
            println("Error! " + "The command \"" + params["command"] + "\" is producing the following error: " + it.getBuffer())
        }

        expect _re(powerUserPrompt + "\$") {
            status = "1"
        }


    } else {
        println("Error Not logged in Configuration User Mode!")

    }
}



send "exit" + defaultTerminator
expect eof()

conn.disconnect()
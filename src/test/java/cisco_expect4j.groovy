

import net.itransformers.expect4java.cliconnection.CLIConnection

/*
 * cisco_expect4j.groovy
 *
 * Copyright 2016  iTransformers Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.apache.log4j.Level
import org.apache.log4j.LogManager




if (args.length == 0){
    LogManager.getRootLogger().setLevel(Level.INFO);
} else {
    LogManager.getRootLogger().setLevel(Level.toLevel(args[0]));
}


modes = ["notLogedIn": 0, "logedIn": 1, "logedInPrivilege15Mode": 2, "logedInConfigMode": 3]

mode = modes["notLogedIn"];

prompt = ">"
powerUserPrompt = "#"
defaultTerminator = "\r"

hostname = ""
status = ["success": 1, "failure": 2];



//evaluate(new File("login.groovy"))
//evaluate(new File("setupTerminal.groovy"))
//evaluate(new File("sendCommand.groovy"))
//evaluate(new File("sendConfigurationCommand.groovy"))
//
//evaluate(new File("logout.groovy"))




if (login() == status["failure"]) {
    exit(conn);

}

if (setupTerminal()){

    if (params["verificationCommand"]!=null){
        return sendPrivilegeModeCommand(params["verificationCommand"]);
    }

    if (params["configCommand"]) {

        if (setConfigMode()!=status["success"]) {

            return sendConfigCommand(params["configCommand"]);
        }
    }

    if (params["configTemplate"]) {

        if (setConfigMode()!=status["success"]) {

            return applyConfigTemplate(params["configTemplate"]);
        }

    }

}
exit(conn);



//config = new Hashtable<String, String>();

//configTemplate = "interface loopback 101\n" +
//        "ip address 127.0.0.101 255.255.255.255\n" +
//        "no shutdown\n";
//
//verificationCommand="sh ip route"

//config.put("StrictHostKeyChecking", "no");
//params = ["config": config, "protocol": "ssh", "username": "nbu", "password": "nbu321!", "enablePass": "nbu321!", "address": "193.19.175.129", "port": 22, "timeout": 30000, "configCommand": "no ip domain-lookup","configTemplate":configTemplate,"verificationCommand":verificationCommand]

//CLIConnection conn = null
//
//
//if (params["protocol"] == "telnet") {
//    conn = new TelnetCLIConnection()
//} else if (params["protocol"] == "raw") {
//    conn = new RawSocketCLIConnection()
//} else {
//    conn = new SshCLIConnection()
//}
//
//conn.connect(params)
//
//Expect4Groovy.createBindings(conn, getBinding(), true)
//    commandExecutionResult = sendConfigCommand(params["configCommand"]);
//
//    println(commandExecutionResult)
//
//    if (commandExecutionResult.get("status") == status["success"]) {
//        println("----------Command " + params["configCommand"] + " successfully executed !----------");
//    } else {
//        println("----------Command " + params["configCommand"] + " failed !----------");
//        println(commandExecutionResult.get("data"))
//
//    }
//
//    templateExecutionResult = applyConfigTemplate(params["configTemplate"]);
//
//    if (templateExecutionResult.get("status") == status["success"]) {
//        println("----------Command " + configTemplate + " successfully executed !----------");
//    } else {
//        println("----------Command " + configTemplate + " failed !----------");
//        println(templateExecutionResult.get("data"))
//
//    }
//
//    goFromConfigToPrivilegedUserMode();
//
//    println("Send privilege mode command: " + sendPrivilegeModeCommand(params["verificationCommand"]));

//}



def login() {
    def returnFlag = status["failure"];

    if (params["protocol"] != "ssh") {

        if (user() == status["success"]) {

            if (password() == status["success"]) {
                mode = modes["logedIn"];

                showPrivilegeResult = showPrivilege();

                if (showPrivilegeResult.get("status") == status["success"]) {
                    mode = modes["logedInPrivilege15Mode"];
                    returnFlag= status["success"]
                } else {
                    returnFlag = status["failure"]
                }

            } else {
                returnFlag = status["failure"]

            }
        } else {
            returnFlag = status["failure"]
        }
    } else {

        checkTerminalResult  =  checkTerminal();
        if (checkTerminalResult.get("status")==status["success"]) {
            println("----------Login Successful----------")

        }
        if (mode == modes["logedInPrivilege15Mode"]) {
            println("----------Login in Privileged Mode Successful----------")

        } else {
            def enablePasswordResult = enablePassword();
            if (enablePasswordResult.get("status") == status["success"]) {

                showPrivilegeResult = showPrivilege();
                if (showPrivilegeResult.get("status") == status["success"]) {

                    mode = modes["logedInPrivilege15Mode"];

                    println("Login in Privileged Mode Successful")
                    returnFlag = status["success"]

                } else {
                    returnFlag = status["failure"]
                }

            } else {
                println("----------Error - Can't loginCommands to privileged mode----------")
                returnFlag = status["failure"];
            }
        }


    }

    return  returnFlag;
}




def user() {
    def returnFlag = status["failure"];

    expect _re("^Username:|^User:") {
        send params["username"] + defaultTerminator
        returnFlag = status["success"]
    }
    return returnFlag;

}


def password() {
    def returnFlag = status["failure"];

    expect("^Password:") {
        send params["password"] + defaultTerminator
        expect _re(hostname + "(" + prompt + "\$)" + "|" + "(" + powerUserPrompt + "\$" + ")") {
            println("Password accepted")
            it.exp_continue();
            returnFlag = status["success"]
        }
    }

    return returnFlag;
}

def showPrivilege() {
    def returnFlag = status["failure"];
    String privilegeLevel;

    println("----------Check the current privilege level----------");
    send "show privilege" + defaultTerminator
    expect _re("Current privilege level is (\\d+)") {
        returnFlag = status["success"];
        privilegeLevel = it.getMatch(1);
        println("----------Current privilege is " + privilegeLevel + "----------")

    }
    return ["status": returnFlag, "data": privilegeLevel];

}


def enablePassword() {
    def returnFlag = status["failure"];
    String result;

    send "enable" + defaultTerminator;

    expect("Password:") {
        println("----------Sending the enable password----------")
        send params["enablePass"] + defaultTerminator
        expect([
                _re("% Access denied.*") {
                    println("----------Access denied----------")
                    result = it.getBuffer();
                    returnFlag = status["failure"];


                },
                _re(hostname + powerUserPrompt + "\$") {
                    println("----------Enable password accepted----------")
                    mode = modes["logedInPrivilege15Mode"];
                    returnFlag = status["success"];
                }
        ]);

    }

    return ["status": returnFlag, "data": result];

}


def exit(CLIConnection conn) {
    if (mode == modes["logedInConfigMode"]) {
        send "end" + defaultTerminator;
        expect _re(hostname + powerUserPrompt + "\$") {
            send "exit";

        }
    } else {
        send "exit";
    }
    conn.disconnect()
}

def sendPrivilegeModeCommand(command) {
    def returnFlag = status["failure"];

    String result = null
    if (mode == modes["logedInPrivilege15Mode"]) {
        //    send "show run" + "\r"
        send command + defaultTerminator

        expect _re(hostname + powerUserPrompt + "\$") {
            returnFlag = status["success"]
            result = it.getBuffer()
        }

    } else {
        println("----------Error Not logged in Privilege User Mode!----------")

    }
    return ["status": returnFlag, "data": result]


}

def sendUnprivilegedModeCommand(command) {
//Send verification command
    def returnFlag = status["failure"];

    String result = null
    if (mode == modes["logedIn"]) {
        //    send "show run" + "\r"
        send command + defaultTerminator

        expect _re(hostname + prompt + "\$") {
            returnFlag = status["success"]
            result = it.getBuffer()
        }

    } else {
        println("----------Error Not logged in  User Mode!----------")

    }
    return ["status": returnFlag, "data": result]


}

def setConfigMode() {

    if (mode == modes["logedInPrivilege15Mode"]) {
        send "configure terminal" + defaultTerminator

        expect([

                _re("\\nEnter configuration commands, one per line.  End with CNTL/Z.\r") {

                    println("----------We have matched that one!!!----------")

                    it.exp_continue();
                },

                _re(hostname + "\\(" + "config" + "\\)" + powerUserPrompt + "\$") {

                    println("----------We are in config mode.!!!----------")
                    mode = modes["logedInConfigMode"];

                }
        ]);
    } else {
        println("----------Error Not logged in Privilege User Mode!----------")
        return status["failure"];

    }

    return status["failure"];

}

def sendConfigCommand(commandLine) {

    returnStatus = status["failure"];
    String result;

    if (mode == modes["logedInConfigMode"]) {

        send commandLine + defaultTerminator

        expect([
                _re(commandLine + "\r\n") {
                    it.exp_continue();
                },
                _re("% Invalid input detected.*") {

                    returnStatus = status["false"];
                    result = it.getBuffer()

                },
                _re(hostname + "\\(" + "config" + ".*" + "\\)" + powerUserPrompt + "\$") {

                    returnStatus = status["success"];


                }
        ]

        );


    } else {

        println("----------Error Not logged in Config Mode!----------")

    }

    return ["status": returnStatus, "data": result]


}


def applyConfigTemplate(template) {
    returnStatus = status["failure"];
    String result;

    if (mode == modes["logedInConfigMode"]) {

        def configCommands = template.split("\n");

        for (String configCommand : configCommands) {

            commandResult = sendConfigCommand(configCommand);

            if (commandResult.get("status") == status["success"]) {
                continue;
            } else {
                println("error executing command: " + configCommand + "!!!")
                result = commandResult.get("data");

            }
        }


    }

    return ["status": returnStatus, "data": result]
}





def setupTerminal() {



    println("----------Trying to setup the terminal----------");

    def returnFlag = status["failure"];

    if (mode == modes["logedInPrivilege15Mode"]) {
        send "terminal length 0" + defaultTerminator

        expect _re(hostname + powerUserPrompt + "\$") {
            returnFlag = status["success"];

        }


    } else {
        println("----------Error Not logged in Privilege User Mode!----------")

    }
    return returnFlag;
}

def checkTerminal() {
    def returnFlag = status["failure"];
    int ciscoDeviceMode;
    expect([
            _re("\r\n(.*)>" + "\$") {

                hostname = it.getMatch(1);
                ciscoDeviceMode = modes["logedIn"];
                returnFlag = status["success"];

            },
            _re("\r\n(.*)#" + "\$") {
                hostname = it.getMatch(1);
                ciscoDeviceMode = modes["logedInPrivilege15Mode"];
                returnFlag = status["success"];
            }
    ]);

    return ["status": returnFlag, "data": ciscoDeviceMode ];

}


def goFromConfigToPrivilegedUserMode() {
    def returnFlag = status["failure"];
    int ciscoDeviceMode;
    if (mode == modes["logedInConfigMode"]) {
        send "end" + defaultTerminator
        expect _re(hostname + powerUserPrompt + "\$") {

            ciscoDeviceMode = modes["logedInPrivilege15Mode"];
            returnFlag = status["success"];
        }

    } else {
        println("----------Error Not logged in Privilege User Mode!----------")

    }
    return ["status": returnFlag, "data": ciscoDeviceMode ];
}




















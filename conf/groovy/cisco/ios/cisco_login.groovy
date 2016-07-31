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

import net.itransformers.expect4java.cliconnection.CLIConnection


import org.apache.log4j.Level
import org.apache.log4j.LogManager


if (params.get("LOGGING_LEVEL")!=null) {
    LogManager.getRootLogger().setLevel(Level.INFO);
} else {
    LogManager.getRootLogger().setLevel(Level.toLevel(params.get("LOGGING_LEVEL")));
}


modes = ["notLogedIn": 0, "logedIn": 1, "logedInPrivilege15Mode": 2, "logedInConfigMode": 3]

mode = modes["notLogedIn"];

status = ["success": 1, "failure": 2]
modes = ["notLogedIn": 0, "logedIn": 1, "logedInPrivilege15Mode": 2, "logedInConfigMode": 3]
mode = params["mode"];


prompt = params.get("prompt");
powerUserPrompt = params.get("powerUserPrompt");
defaultTerminator = params.get("defaultTerminator");

hostname = '';



def result;


if (login() == status["failure"]) {
    result = ["status": 2, "data": "Login Failure!","mode":mode]

} else {
    if (setupTerminal()==status["success"]) {

        result = ["status": 1, "data": "Login Success!", "hostname": hostname,"mode":mode]
    } else{
        result = ["status": 2, "data": "Can't setup terminal!","mode":mode]
    }
}

return result;

if (setupTerminal()==status["success"]) {

    if (params["verificationCommand"] != null) {
        sendPrivilegeModeCommandResult = sendPrivilegeModeCommand(params["verificationCommand"]);
        exit();

        return sendPrivilegeModeCommandResult;
    }

    if (params["configCommand"]) {

        if (setConfigMode() != status["success"]) {
            sendConfigCommandResult = sendConfigCommand(params["configCommand"]);
            exit();

            return sendConfigCommandResult;
        }
    }

    if (params["configTemplate"]) {

        if (setConfigMode() != status["success"]) {

            applyConfigTemplateResult = applyConfigTemplate(params["configTemplate"]);
            exit();


            return applyConfigTemplateResult;
        }

    }

}

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

                if (mode == modes["logedInPrivilege15Mode"]) {
                    println("----------Login in Privileged Mode Successful----------")
                } else {
                    def enablePasswordResult = enablePassword();
                    if (enablePasswordResult.get("status") == status["success"]) {

                        showPrivilegeResult = showPrivilege();

                        if (showPrivilegeResult.get("status") == status["success"]) {
                            mode = modes["logedInPrivilege15Mode"];
                            returnFlag = status["success"]
                        } else {
                            returnFlag = status["failure"]
                        }
                    } else {
                        println("----------Error - Can't login to privileged mode----------")
                        returnFlag = status["failure"];

                    }
                }


            } else {
                println("----------Error - Can't login ----------")

                returnFlag = status["failure"]

            }
        } else {
            println("----------Error - Can't send username ----------")

            returnFlag = status["failure"]
        }
    } else {

        checkTerminalResult = checkTerminal();
        if (checkTerminalResult.get("status") == status["success"]) {
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

                    println("----------Login in mode with privilege: " + showPrivilegeResult.get("data") + "is successful----------")
                    returnFlag = status["success"]

                } else {
                    returnFlag = status["failure"]
                }

            } else {
                println("----------Error - Can't login to privileged mode----------")
                returnFlag = status["failure"];
            }
        }


    }

    return returnFlag;
}


def user() {
    def returnFlag = status["failure"];

    expect _re("Username:|User:") {
        send params["username"] + defaultTerminator
        returnFlag = status["success"]

        if (params["protocol"]=='telnet'){
            expect _re(params["username"]+"\r\n"){

                println("----------Expect echo username----------")

            }
        }
    }

    return returnFlag;

}


def password() {
    def returnFlag = status["failure"];

    expect _re("Password:") {
        send params["password"] + defaultTerminator

        if (params["protocol"]=='telnet'){
            expect ([ _re(params["password"]+defaultTerminator){

                println("----------Expect echo password----------")

            },
                      _timeout(1000l){

                      }
            ])
        }

        checkTerminalResult = checkTerminal();

        if (checkTerminalResult.get("status") == status["success"]) {
            returnFlag = status["success"];

        }

    }

    return returnFlag;
}

def showPrivilege() {
    def returnFlag = status["failure"];
    String privilegeLevel;

    send "show privilege" + defaultTerminator
    expect ([

            _re("show privilege" + defaultTerminator){
                println("----------Matching the echo show-privilege---------");
                it.exp_continue();
            },

            _re("Current privilege level is (\\d+)") {
                privilegeLevel = it.getMatch(1);
                it.exp_continue();
            },
            _re(hostname+powerUserPrompt){

                returnFlag = status["success"];
            }
    ]);

    return ["status": returnFlag, "data": privilegeLevel];

}


def enablePassword() {
    def returnFlag = status["failure"];
    String result;

    send "enable" + defaultTerminator;


    expect("Password:") {
        println("----------Sending the enable password----------")
        send params["enable-password"] + defaultTerminator;
        sleep(1000);
        expect([
                _re (params["enable-password"]+defaultTerminator){
                    println("----------Matching the echo enable password---------");

                    it.exp_continue();
                },
                _re("% Access denied.*") {
                    println("----------Access denied----------")
                    result = it.getBuffer();
                    returnFlag = status["failure"];
                },

//                _re("\\r\\n"){
//                    println("----------Matched a banner2!!!----------")
//                    it.exp_continue();
//                },
                _re(hostname + powerUserPrompt) {
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
}

def sendPrivilegeModeCommand(command) {
    def returnFlag = status["failure"];
    String result = null

    if (cleanTerminal() == status["success"]) {


        if (mode == modes["logedInPrivilege15Mode"]) {
//            expect([
//                    _re(".*") {
//                        println("----------Buffer Before Config: " + it.getBuffer());
//                    }, _timeout(1000) {
//
//            }]);

            send command + defaultTerminator;


            expect([
                    _re(command+defaultTerminator+"\n"){
                        println("----------Match echo of the command: " + command + "----------" );
                        it.exp_continue();
                    },

                    _re(hostname + powerUserPrompt) {
                        returnFlag = status["success"]
                        //  println("----------Commnd Output: " + it.getBuffer() + "----------")
                        result = it.getBuffer();
                    },

            ]);

        } else {
            println("----------Error Not logged in Privilege User Mode!----------")

        }
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

                _re("Enter configuration commands, one per line.  End with CNTL/Z.") {
                    println("Match a warining");
                    it.exp_continue();
                },

                _re(hostname + ".*" + powerUserPrompt) {

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
                _re(powerUserPrompt + "\$") {

                    returnStatus = status["success"];


                }
        ]

        );


    } else {

        println("----------Error Not logged in Config Mode!----------")

    }

    return ["status": returnStatus, "data": result,"mode":mode]


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
        send "terminal length 0" + defaultTerminator;

        expect([

                _re ("terminal length 0" + defaultTerminator){
                    println("----------Terminal 0 echo matched!----------");

                    it.exp_continue();
                },
                _re(hostname + powerUserPrompt + "\$") {
                    println("----------Terminal has been setup!----------");
                    returnFlag = status["success"];
                }]);



    } else {
        println("----------Error Not logged in Privilege User Mode!----------")

    }
    return returnFlag;
}

def cleanTerminal() {
    def returnFlag = status["failure"];
    println("----------Sending \\r to the terminal----------")
    send defaultTerminator;
    expect ([
            _re(defaultTerminator){
                println("----------Matching the echo of \\r----------");
                it.exp_continue();
            },
            _re(hostname+powerUserPrompt) {
                println("----------Matching the "+hostname+powerUserPrompt + "----------");

                returnFlag = status["success"];
            }
    ]);

    return returnFlag;
}

def checkTerminal() {
    def returnFlag = status["failure"];
    int ciscoDeviceMode;
    expect([
            //TODO avoid banners here
            _re("banner"){
                println("----------Matched some banner----------")
                it.exp_continue();
            },
            _re("\r\n"){
                println("----------Matched on a \\r \\n----------")

                it.exp_continue();
            },
            _re("(.*)>\$") {

                hostname = it.getMatch(1);
                println("----------Matched on > prompt. Setting hostname to "+hostname+"----------")

                ciscoDeviceMode = modes["logedIn"];
                returnFlag = status["success"];


            },
            _re("(.*)#\$") {


                hostname = it.getMatch(1);
                println("----------Matched on # prompt. Setting hostname to "+hostname+"----------")

                ciscoDeviceMode = modes["logedInPrivilege15Mode"];
                returnFlag = status["success"];
            }
    ]);

    return ["status": returnFlag, "data": ciscoDeviceMode];

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
    return ["status": returnFlag, "data": ciscoDeviceMode];
}

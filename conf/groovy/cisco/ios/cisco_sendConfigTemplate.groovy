/*
 * cisco_sendConfigCommand.groovy
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

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 1/23/14
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */


status = ["success": 1, "failure": 2]
modes = ["notLogedIn": 0, "logedIn": 1, "logedInPrivilege15Mode": 2, "logedInConfigMode": 3]
mode = params["mode"];


prompt = params.get("prompt");
powerUserPrompt = params.get("powerUserPrompt");
defaultTerminator = params.get("defaultTerminator");

hostname = params.get("hostname");


def result;

if (params.get("configTemplate")){

    configModeResult = setConfigMode();

    if (configModeResult.get("status") != status["success"]) {

        result = applyConfigTemplate(params["configTemplate"]);
    } else {

        result = configModeResult;

    }
}

return result;


def sendConfigCommand(commandLine) {

    returnStatus = status["failure"];
    String commandResult;

    returnStatus1 = status["success"];

    if (mode == modes["logedInConfigMode"]) {

        send commandLine + defaultTerminator

        expect([
                _re(commandLine + "\r\n") {
                    it.exp_continue();
                },
                _re("% Invalid input detected.*\r\n") {

                    returnStatus1 = status["failure"];
                    commandResult = it.getBuffer()
                    it.exp_continue();


                },
                _re("Not a valid.*\r\n"){
                    returnStatus1 = status["failure"];
                    commandResult = it.getBuffer()
                    it.exp_continue();

                },
                _re(powerUserPrompt + "\$") {
                    result = it.getBuffer();
                    if (returnStatus1==status["success"]){
                        returnStatus = status["success"];
                    }

                }
        ]

        );


    } else {

        println("----------Error Not logged in Config Mode!----------")

    }

    return ["status": returnStatus, "data": result,"mode": mode]


}


def setConfigMode() {
    returnStatus = status["failure"];
    String result;

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
                    returnStatus = status["success"];
                    result="We are in config mode.!!!";

                }
        ]);
    } else {
        println("----------Error Not logged in Privilege User Mode!----------")
        result = "Error Not logged in Privilege User Mode!";
    }

    return ["status": returnStatus, "data": result,"mode": mode]

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
                break;

            }
        }


    }

    return ["status": returnStatus, "data": result,"hostname":hostname];
}





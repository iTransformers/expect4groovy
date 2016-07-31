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

if (params.get("configCommand")) {

    configModeResult = setConfigMode();

    if (configModeResult.get("status") != status["success"]) {

        result = sendConfigCommand(params["configCommand"]);
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
                    } else{
                        result = commandResult;
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

            }
        }


    }

    return ["status": returnStatus, "data": result]
}









//def result = sendCommand()
//
//return result
//
//def sendCommand() {
//    def returnFlag = 2
//    def result = null
//    if (params["configMode"] == true) {
//        String command = params["command"]
//        send(command + defaultTerminator)
//        expect(command + defaultTerminator){
//            println("command sent")
//            commandResult = it.getBuffer()
//
//        }
//        expect([
//                _re(params["hostname"]+"\\(config\\)"+powerUserPrompt) {
//                    println("Command "+command+" successfully executed")
//                    it.getBuffer()
//                    returnFlag = status["success"]
//                }
//        ]);
//    } else {
//        send("conf t" + defaultTerminator)
//        println(System.currentTimeMillis())
//        expect([ _re("conf t" + defaultTerminator){
//        }]);
//        expect(defaultTerminator){
////            println("confwwt."+System.currentTimeMillis())
//        };
//        expect(params["hostname"]+"(config)"+powerUserPrompt){
//            println("Entering Config mode."+System.currentTimeMillis())
//            configMode = true;
//
//        }
//        if(configMode ==true){
//            String command = params["command"];
//            send(command + defaultTerminator)
//            expect(command + defaultTerminator){
//                println("command sent")
//                commandResult = it.getBuffer()
//            };
//            expect([
////                    _re(".*"){
////
////                    },
//                    //Ne moga da razbera zashto ne machva dolnia red
//                    _re(params["hostname"]+"\\(config\\)"+powerUserPrompt) {
////                    _re(".*"){
//                        it.getBuffer();
//                        println("Command "+command+" successfully executed")
//                        returnFlag = status["success"]
//                    }
//            ]);
//
//        }else{
//            println("Can't enter in Config Mode!!!")
//            returnFlag = false;
//
//        }
//    }
//    return ["status": returnFlag, "commandResult": commandResult, "configMode": configMode]
//
//
//}
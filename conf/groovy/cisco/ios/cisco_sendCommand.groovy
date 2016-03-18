/*
 * cisco_sendCommand.groovy
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


prompt = ">"
powerUserPrompt = "#"
defaultTerminator = "\r"
logedIn = "false"
logedInPowerMode = "false"
logedInConfigMode = false
configPrompt = null;
hostname = ""
status = ["success": 1, "failure": 2]

def result = sendCommand()

return result

def sendCommand() {
    def returnFlag = 2
    def result = null
    String commandResult = command;
    send(command + defaultTerminator)
    expect (command+defaultTerminator)
 //   iterator().getBuffer();
    if (evalScript != null) {
       // println(System.currentTimeMillis())
        def evalResult = evaluate(evalScript)
        if (evalResult["status"] != status["success"]) {
            return ["status": returnFlag, "reportResult": evalResult["reportResult"], "commandResult": evalResult["commandResult"]]
        }
    }
    expect([
        _re(params["hostname"]+powerUserPrompt + "\$") {
            returnFlag = status["success"]
            commandResult = it.getBuffer()
        }

    ]);
    return ["status": returnFlag, "commandResult": commandResult]


}
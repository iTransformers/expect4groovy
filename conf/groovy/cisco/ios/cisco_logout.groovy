println (System.currentTimeMillis())

/*
 * cisco_logout.groovy
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

hostname = params.get("hostname");
status = ["success": 1, "failure": 2]
modes = ["notLogedIn": 0, "logedIn": 1, "logedInPrivilege15Mode": 2, "logedInConfigMode": 3]
mode = params["mode"];



def result;

result = exit();

return  result;

def exit(){
    returnStatus = status["failure"];
    def result;

    if (mode == modes["logedInConfigMode"]) {
        send "end" + defaultTerminator;

        expect ([

                _re(powerUserPrompt) {
                    send "exit";
                    result = ["status": status["success"], "data": "Logout Success!"]
                },
                _re("end" + "\r"){
                    result = ["status": status["success"], "data": "Logout Success!"]
                    send "exit";
                    it.exp_continue();

                },

            ]);




    } else {
        send "exit";
        result = ["status": status["success"], "data": "Logout Success!"]

    }
    return  result;
}
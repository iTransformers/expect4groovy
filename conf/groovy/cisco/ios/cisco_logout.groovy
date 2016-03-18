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
logedIn = "false"
logedInPowerMode = "false"
logedInConfigMode = false
hostname = ""
status = ["success": 1, "failure": 2]

exit()

def exit(){
    if (params["configMode"] == true) {
        send(""+(char)0x1A) // ^z
        expect(params["hostname"]+powerUserPrompt)
        send("exit"+defaultTerminator)
        expect _eof()
    }
    return ["status": 1, "data": "Logout Success!"]
}
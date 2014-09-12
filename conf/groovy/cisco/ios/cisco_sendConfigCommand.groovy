/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 1/23/14
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */


prompt = ">"
powerUserPrompt = "#"
defaultTerminator = "\r\n"
logedIn = "false"
logedInPowerMode = "false"
logedInConfigMode = false
configMode = params["configMode"];
hostname = ""
status = ["success": 1, "failure": 2]
commandResult = ""

def result = sendCommand()

return result

def sendCommand() {
    def returnFlag = 2
    def result = null
    if (params["configMode"] == true) {
        String command = params["command"]
        send(command + defaultTerminator)
        expect(command + defaultTerminator){
            println("command sent")
            commandResult = it.getBuffer()

        }
        expect([
                _re(params["hostname"]+"\\(config\\)"+powerUserPrompt) {
                    println("Command "+command+" successfully executed")
                    it.getBuffer()
                    returnFlag = status["success"]
                }
        ]);
    } else {
        send("conf t" + defaultTerminator)
        println(System.currentTimeMillis())
        expect([ _re("conf t" + defaultTerminator){
        }]);
        expect(defaultTerminator){
//            println("confwwt."+System.currentTimeMillis())
        };
        expect(params["hostname"]+"(config)"+powerUserPrompt){
            println("Entering Config mode."+System.currentTimeMillis())
            configMode = true;

        }
        if(configMode ==true){
            String command = params["command"];
            send(command + defaultTerminator)
            expect(command + defaultTerminator){
                println("command sent")
                commandResult = it.getBuffer()
            };
            expect([
//                    _re(".*"){
//
//                    },
                    //Ne moga da razbera zashto ne machva dolnia red
                    _re(params["hostname"]+"\\(config\\)"+powerUserPrompt) {
//                    _re(".*"){
                        it.getBuffer();
                        println("Command "+command+" successfully executed")
                        returnFlag = status["success"]
                    }
            ]);

        }else{
            println("Can't enter in Config Mode!!!")
            returnFlag = false;

        }
    }
    return ["status": returnFlag, "commandResult": commandResult, "configMode": configMode]


}
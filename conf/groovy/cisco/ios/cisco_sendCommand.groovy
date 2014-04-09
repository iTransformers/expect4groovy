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
logedInConfigMode = "false"
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
    expect _re(params["hostname"]+powerUserPrompt + "\$") {
        returnFlag = status["success"]
        commandResult = it.getBuffer()
    }
    return ["status": returnFlag, "commandResult": commandResult]


}
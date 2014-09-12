prompt = ">"
powerUserPrompt = "#"
defaultTerminator = "\r"
logedIn = "false"
logedInPowerMode = "false"
logedInConfigMode = false
hostname = ""
status = ["success": 1, "failure": 2]

send "pwd\n"
expect _gl("/home/vvv") {
    println "Got it"
}

expect _gl("~>") {
    println ("Finished")
}
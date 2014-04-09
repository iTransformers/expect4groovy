status = ["success": 1, "failure": 2]

def reportResult = [:]
def evalresult = [:]

def result = status["failure"]
StringBuffer stringBuffer = new StringBuffer();
Map<String,String> serviceFinger = ["message":"Service Finger has been disabled!","score":0];
Map<String,String> servicePad = ["message":"Service Pad has been enabled!","score":0];
Map<String,String> serviceUdpSmallServices = ["message":"Service UDP small services has been enabled!","score":0];
Map<String,String> serviceTcpSmallServices = ["message":"Service TCP small services has been enabled!","score":0];
Map<String,String> servicePasswordEncryption = ["message":"Service Password Encryption has been disabled!","score":0];
Map<String,String> serviceTcpKeepalivesIn = ["message":"Service TCP keepalives-in has been disabled!","score":0];
Map<String,String> serviceTcpKeepalivesOut = ["message":"Service TCP keepalives-Out has been disabled!","score":0];
Map<String,String> cdpRun = ["message":"Cisco Discovery Protocol has been enabled!","score":0];
Map<String,String> ipBootPserver = ["message":"IP BootP server has been enabled","score":0];
Map<String,String> ipHttpServer = ["message":"IP HTTP server has been enabled","score":0];
Map<String,String> ipFinger = ["message":"IP finger has been enabled","score":0];
Map<String,String> ipSourceRoute = ["message":"IP Source Routing has been enabled!","score":0];
Map<String,String> ipGratuitousArps = ["message":"IP Gratuitous ARPs have been enabled","score":0];
Map<String,String> ipIdentd = ["message":"IP IdentD has been enabled!","score":0];
Map<String,String> securityPasswordsMinLength = ["message":"Passoword security min length has been diabled","score":0];
Map<String,String> authenticationFailureRate = ["message":"Security authentication failure rate has been disabled!","score":0];
Map<String,String> enableSecret = ["message":"Enable secret has not been configured!","score":0];
Map<String,String> aaaNewModel = ["message":"AAA new model has not been used!","score":0];
Map<String,String> aaaLocal = ["message":"New Model AAA local authentication has not been used!","score":0];
Map<String,String> serviceSequenceNumbers = ["message":"Service sequence-numbers has been disabled!","score":0];
Map<String,String> loggingConsoleCritical = ["message":"All kinds of error logging to console has been enabled!","score":0];
Map<String,String> loggingBuffered = ["message":"Log output has not been buffered!","score":0];
//Map<String,String> interfaces = [:];

int points = 0;
int totalPoints = 180;
expect ([
//    _re("username " + params["username"]) {
//        stringBuffer.append(it.getBuffer())
//        result = status["success"]
//        if (result == status["success"]) {
//            reportResult.put("username", ["status": status["success"], "message": "Username " + params["username"] + " evaluation sucessful!"])
//        } else {
//            reportResult.put("username", ["status": status["failure"], "message": "Username " + params["username"] + " evaluation failed!"])
//        }
//        it.exp_continue()
//    },

//Global services
        _re("service finger") {
            stringBuffer.append(it.getBuffer())
            points = points +10;
            serviceFinger = ["message": "Service Finger has been enabled!","score":10];
            it.exp_continue()
        },

        _re("no service pad") {
            stringBuffer.append(it.getBuffer())
            servicePad = ["message":"Service Pad has been disabled!","score":10];
            points = points +10;

            it.exp_continue()
        },
        _re("no service udp-small-servers") {
            stringBuffer.append(it.getBuffer())
            serviceUdpSmallServices = ["message":"Service UDP small services has been disabled!","score":10];
            points = points +10;

            it.exp_continue()
        },
        _re("no service tcp-small-servers") {
            stringBuffer.append(it.getBuffer())
            serviceTcpSmallServices = ["message":"Service TCP small services has been disabled!","score":10];
            points = points +10;

            it.exp_continue()
        },

        _re("service password encryption") {
            stringBuffer.append(it.getBuffer())
            servicePasswordEncryption = ["message":"Service Password Encryption has been enabled!","score":10];
            points = points +10;

            it.exp_continue()
        },
        _re("service tcp-keepalives-in") {
            stringBuffer.append(it.getBuffer())
            serviceTcpKeepalivesIn = ["message":"Service TCP keepalives-in has been enabled!","score":10];
            points = points +10;

            it.exp_continue()
        },
        _re("service tcp-keepalives-out") {
            stringBuffer.append(it.getBuffer())
            serviceTcpKeepalivesOut = ["message":"Service TCP keepalives-out has been enabled!","score":10];
            points = points +10;

            it.exp_continue()
        },
        _re("no cdp run") {
            stringBuffer.append(it.getBuffer())
            cdpRun = ["message":"Cisco Discovery Protocol has been disabled!","score":10];
            points = points +10;

            it.exp_continue()
        },
        _re("no ip bootp server") {
            stringBuffer.append(it.getBuffer())
            ipBootPserver = ["message":"IP BootP server has been disabled!","score":10];
            points = points +10;

            it.exp_continue()
        },
        _re("no ip http server") {
            stringBuffer.append(it.getBuffer())
            ipHttpServer = ["message":"IP HTTP server has been disabled!","score":10];
            points = points +10;

            it.exp_continue()
        },
        _re("no ip finger") {
            stringBuffer.append(it.getBuffer())
            ipFinger = ["message":"IP Finger has been enabled!","score":10];
            points = points + 10;

            it.exp_continue()
        },
//        _re("interface (.*)") {
//            stringBuffer.append(it.getBuffer())
//            String interfaceName = it.getMatch(1);
//            Map<String,String>  interfaceEval = [:];
//            Map<String,String> ipRedirects = ["message":"IP redirects has been enabled","score":0];
//            Map<String,String> ipUnreachables = ["message":"IP Unreachables has been enabled","score":0];
//            Map<String,String> ipProxyArp = ["message":"IP Proxy ARP has been enabled","score":0];
//
//            expect ([
//            _re("no ip redirects"){
//                stringBuffer.append(it.getBuffer())
//                ipRedirects = ["message":"IP redirects has been disabled!","score":10];
//                points = points +10;
//                totalPoints = totalPoints + 10;
//                it.exp_continue()
//            },
//            _re("no ip unreachables"){
//                stringBuffer.append(it.getBuffer())
//                ipUnreachables = ["message":"IP unreachables has been disabled!","score":10];
//                points = points +10;
//                totalPoints = totalPoints + 10;
//                it.exp_continue()
//            },
//            _re("no ip proxy-arp"){
//                stringBuffer.append(it.getBuffer())
//                ipProxyArp = ["message":"IP proxy arp has been disabled!","score":10];
//                points = points +10;
//                totalPoints = totalPoints + 10;
//                it.exp_continue()
//            }])
//            interfaceEval.put("ipProxyArp",ipProxyArp);
//            interfaceEval.put("ipUnreachables",ipUnreachables);
//            interfaceEval.put("ipProxyArp",ipProxyArp);
//            interfaces[interfaceName,interfaceEval]
//            it.exp_continue()
//        },
        _re("no ip source-route") {
            stringBuffer.append(it.getBuffer())
            ipSourceRoute = ["message":"IP Source Routing has been disabled!","score":10];
            points = points +10;

            it.exp_continue()
        },
        _re("no ip gratuitous-arps") {
            stringBuffer.append(it.getBuffer())
            ipGratuitousArps = ["message":"IP Gratuitous ARPs has been disabled!","score":10];
            points = points +10;

            it.exp_continue()
        },
        _re("no ip identd") {
            stringBuffer.append(it.getBuffer())
            ipIdentd = ["message":"IP IdentD has been disabled!","score":10];
            points = points +10;

            it.exp_continue()
        },
        _re("security passwords min-length (\\d)") {
            stringBuffer.append(it.getBuffer())
            passwordLength = it.getMatch(1);
            Float pointScore = passwordLength?.isInteger()?passwordLength.toFloat():null
            securityPasswordsMinLength = ["message":"Passoword security min length of "+ passwordLength + " symbols has been configured!","score":pointScore];
            points = points + pointScore;

            it.exp_continue()
        },
        _re("security authentication failure rate (\\d) log ") {
            stringBuffer.append(it.getBuffer())
            authenticationFailureRate = ["message":"Security authentication failure rate of "+ it.getMatch(1) + " has been enabled!","score":10];
            points = points +10;

            it.exp_continue()
        },
        _re("enable secret (\\d)") {
            stringBuffer.append(it.getBuffer())
            enableSecret = ["message":"Enable password has been encrypted with "+ it.getMatch(1) + " level of security!","score":10];
            points = points +10;

            it.exp_continue()
        },
        _re("service sequence-numbers") {
            stringBuffer.append(it.getBuffer())
            serviceSequenceNumbers = ["message":"Service sequence-numbers has been enabled!","score":10];
            points = points +10;

            it.exp_continue()
        },
        _re("aaa new-model") {
            stringBuffer.append(it.getBuffer())
            aaaNewModel = ["message":"AAA new-model is enabled!" ,"score":10];
            points = points +10;

            it.exp_continue()
        },
        _re("aaa authentication login local_auth local") {
            stringBuffer.append(it.getBuffer())
            aaaLocal = ["message":"AAA new Model local authentication has been enabled!","score":10];
            points = points +10;

            it.exp_continue()
        },

        _re("logging console critical") {
            stringBuffer.append(it.getBuffer())
            loggingConsoleCritical = ["message":"Logging of critical errors to console has been enabled!","score":10];
            points = points +10;

            it.exp_continue()
        },
        _re("logging buffered") {
            stringBuffer.append(it.getBuffer())
            loggingBuffered = ["message":"Logging buffered has been enabled!" ,"score":10];
            points = points +10;

            it.exp_continue()
        },
        _re(params["hostname"]+powerUserPrompt + "\$") {
            stringBuffer.append(it.getBuffer())
            returnFlag = status["success"]
        },
//    timeout(5l){
//        println ("timeouted, buffer: "+it.getBuffer())
//    }

])

reportResult.put("service finger",serviceFinger);
reportResult.put("service pad",servicePad);
reportResult.put("UDP small services",serviceUdpSmallServices);
reportResult.put("TC small services",serviceTcpSmallServices);
reportResult.put("Password Encryption",servicePasswordEncryption);
reportResult.put("TCP in keep alives",serviceTcpKeepalivesIn);
reportResult.put("TCP out keep alives",serviceTcpKeepalivesOut);
reportResult.put("CDP",cdpRun);
reportResult.put("ipBootPserver",ipBootPserver);
reportResult.put("ipHttpServer",ipHttpServer);
reportResult.put("ipFinger",ipFinger);
reportResult.put("ipSourceRoute",ipSourceRoute);
reportResult.put("ipGratuitousArps",ipGratuitousArps);
reportResult.put("ipIdentd",ipIdentd);
reportResult.put("securityPasswordsMinLength",securityPasswordsMinLength);
reportResult.put("authenticationFailureRate",authenticationFailureRate);
reportResult.put("enableSecret",enableSecret);
reportResult.put("aaaNewModel",aaaNewModel);
reportResult.put("aaaNewModel",aaaLocal);
reportResult.put("serviceSequenceNumbers",serviceSequenceNumbers);
reportResult.put("loggingConsoleCritical",loggingConsoleCritical);
reportResult.put("loggingBuffered",loggingBuffered);
//reportResult.put("interfaces":interfaces);
reportResult.put("totalScore",["message": points.toString()+ " out of","score":totalPoints.toString()]);


evalresult["commandResult"] = stringBuffer.toString()
evalresult["reportResult"] = reportResult
return evalresult
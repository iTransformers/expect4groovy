package net.itransformers.expect4groovy

import net.itransformers.expect4java.cliconnection.CLIConnection
import net.itransformers.expect4java.cliconnection.impl.EchoCLIConnection
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

/**
 * Created by vasko on 22.10.15.
 */
class Expect4jWithMetaclassTest {
    Map<String, Object> expObjects

//    @BeforeMethod
//    public void setUp(){
//        CLIConnection conn = new EchoCLIConnection();
//        conn.connect();
//        expObjects = Expect4Groovy.createObjects(conn, true, Expect4jWithMetaclassTest.metaClass)
//    }
//
//    @Test
//    public void testGlob(){
//        send "hello\n"
//        expect _gl("hello\n") {
//            println "Hello World!"
//        }
//    }

}
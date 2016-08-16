package net.itransformers.expect4groovy.simulator

import net.itransformers.expect4groovy.Expect4GroovyScriptLauncher
import net.itransformers.expect4java.cliconnection.CLIConnection
import net.itransformers.expect4java.cliconnection.impl.CrossPipedCLIConnection
import org.testng.Assert
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

/**
 * Created by vasko on 16.08.16.
 */
class TestSimulator4Groovy {

    String path="src/test/java/net/itransformers/expect4groovy/simulator/scripts"
    @Test
    public void doTestWithSimulator() {

        initLogger();

        String[] roots = new String[1];
        roots[0] = path
        def params = ["name": "World"]

        Object result = new Expect4GroovyScriptLauncher().launchWithSimulator(
                roots, "testHelloWorld.groovy", params,
                "simulator.groovy" ,[:]);

        Assert.assertTrue((Boolean) result, "The script is not executed successful")
    }

    private static void initLogger() {
        Logger logger = Logger.getLogger("")
        logger.setLevel(Level.ALL)
        ConsoleHandler handler = new ConsoleHandler() {
            {
                setOutputStream(System.out)
            }
        }
        handler.setLevel(Level.ALL)
        handler.setFormatter(new SimpleFormatter())
        logger.addHandler(handler)
    }

}

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

    @Test
    public void doTestWithSimulator() {

        def params = ["name": "World"]

        new Expect4GroovyScriptLauncher().launchWithSimulator(
                ["src/test/java/net/itransformers/expect4groovy/simulator/scripts"],
                "testHelloWorld.groovy", params,
                "simulator.groovy" ,[:]
        );
    }

}

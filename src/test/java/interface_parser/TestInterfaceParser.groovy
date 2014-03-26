package interface_parser

import expect4j.Expect4j
import net.itransformers.expect4groovy.Expect4GroovyScriptLauncher
import org.junit.Test

import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

class TestInterfaceParser {
    @Test
    public void testInterfaces(){
        initLogger()
        def params = ["protocol":"echo"]
        String[] roots = new String[1];
        roots[0] = "src/test/java/interface_parser/";
        Map result = new Expect4GroovyScriptLauncher().launch(roots, "interface_parser.groovy", params);

        println result

    }

}
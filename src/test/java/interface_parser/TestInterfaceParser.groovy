package interface_parser

import expect4j.Expect4j
import net.itransformers.expect4groovy.Expect4GroovyScriptLauncher
import org.junit.Test

import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 2/3/14
 * Time: 10:52 AM
 * To change this template use File | Settings | File Templates.
 */
class TestInterfaceParser {
    @Test
    public void testInterfaces(){
        Logger logger = Logger.getLogger(Expect4j.class.getName())
        logger.setLevel(Level.ALL)
        ConsoleHandler handler = new ConsoleHandler(){
            {
                setOutputStream(System.out)
            }
        }
        handler.setLevel(Level.ALL)
        handler.setFormatter(new SimpleFormatter())
        logger.addHandler(handler)
        def params = ["protocol":"echo"]
        String[] roots = new String[1];
        roots[0] = "src/test/java/interface_parser/";
        Map result = new Expect4GroovyScriptLauncher().launch(roots, "interface_parser.groovy", params);
        println result

    }
}

// niki vij dali tozi file syshtestvuva
//                                                         /Users/niau/expect4groovy-code/test/java/interface_parser/interface_parser.groovy
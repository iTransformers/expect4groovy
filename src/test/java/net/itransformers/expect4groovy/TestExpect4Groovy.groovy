/*
 * TestExpect4Groovy.groovy
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

package net.itransformers.expect4groovy

import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import java.util.logging.Logger
import java.util.logging.Level
import java.util.logging.ConsoleHandler
import java.util.logging.SimpleFormatter
import org.testng.Assert

class TestExpect4Groovy {

    String path="src/test/java/net/itransformers/expect4groovy/scripts"

    @DataProvider(name = "providerSingle")
    public Object[][] provideAll() throws Exception {
        return [["test_regexp_nested_states.groovy"]];
    }

    @DataProvider(name = "providerAll")
    public Object[][] provide() throws Exception {

        String[] files = new File(path).list(new FilenameFilter(){
            @Override
            boolean accept(File dir, String name) {
                return name.endsWith(".groovy")
            }
        })
        Object[][] result = new Object[files.size()][1]
        files.eachWithIndex { el, i -> result[i][0] = el}
        return result;
    }

    @Test(dataProvider = "providerAll")
    public void doTestAll(String script) {
        doTest(script)
    }

    @Test(dataProvider = "providerSingle")
    public void doTestSingle(String script) {
        doTest(script)
    }

    @Test
    public void doTestInterfaceParser() {
        doTest("interface_parser.groovy")
    }
    @Test
    public void doTestGlob2() {
        doTest("test_glob2.groovy")
    }

    private void doTest(String script) {
        initLogger();
        def params = ["protocol": "echo"]
        String[] roots = new String[1];
        roots[0] = path
        Object result = new Expect4GroovyScriptLauncher().launch(roots, script, params);

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

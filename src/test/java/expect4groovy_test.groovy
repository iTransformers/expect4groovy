/*
 * expect4groovy_test.groovy
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

//def prompt = "#"
//
//expect "user>"
//send "vvv\r"
//
//expect "password>"
//send "123\r"
//expect prompt
//
//send "some command\r"
//expect prompt
//
//send "say hello\r"
//expect ([
// _gl("hello\r"){
//     println("Matched hello")
//     it.exp_continue()
// },
// _re("hello [0-9]+\r"){ expect4j.ExpectState expectState ->
//     println("Matched: "+expectState.getMatch())
//     expectState.exp_continue()
// },
// prompt
//])
//
//
//send "exit\r"
//expect eof()
//
//return ["status": "SUCCESS","data": "Hello"]
package net.itransformers.expect4groovy.simulator.scripts

expect.setTimeout(10000,{
    println "timeouted"
})

expect("hello\n")

expect(params["name"]+"\n");
package net.itransformers.expect4groovy.simulator.scripts

import junit.framework.Assert

expect([
    _gl("hello\n"),
    _timeout(1000){
        Assert.fail("Timeouted")
    }
])

expect([
    _gl(params["name"]+"\n"),
    _timeout(1000){
        Assert.fail("Timeouted")
    }
])

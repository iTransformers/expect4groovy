package net.itransformers.expect4java.matches;

import net.itransformers.expect4java.Closure;

public class TimeoutMatch extends Match{
    Long timeout;

    public TimeoutMatch(Long timeout, Closure closure) {
        super(closure);
        this.timeout = timeout;
    }
}

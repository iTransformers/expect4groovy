package net.itransformers.expect4java.impl;

import net.itransformers.expect4java.ExpectContext;
import net.itransformers.expect4java.matches.Match;
import org.apache.oro.text.regex.MatchResult;

public class ExpectContextImpl implements ExpectContext {
    private MatchResult match;
    private String buffer;
    private boolean expContinue;

    public ExpectContextImpl(MatchResult match, String buffer) {
        this.match = match;
        this.buffer = buffer;
        this.expContinue = false;
    }

    @Override
    public void exp_continue() {
        expContinue = true;
    }

    @Override
    public void exp_continue_reset_timer() {

    }

    @Override
    public String getBuffer() {
        return buffer;
    }

    @Override
    public String getMatch(int groupnum) {
        return match.group(groupnum);
    }

    @Override
    public String getMatch() {
        return match.group(0);
    }

    public boolean isExpContinue() {
        return expContinue;
    }
}

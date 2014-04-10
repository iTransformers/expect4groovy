package net.itransformers.expect4java.impl;

import net.itransformers.expect4java.Closure;
import net.itransformers.expect4java.Expect4j;
import net.itransformers.expect4java.matches.EofMatch;
import net.itransformers.expect4java.matches.Match;
import net.itransformers.expect4java.matches.RegExpMatch;
import net.itransformers.expect4java.matches.TimeoutMatch;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Perl5Matcher;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Expect4jImpl implements Expect4j, Runnable{

    public static final long DEFAULT_TIMEOUT = 1000l;
    private final Reader reader;
    private final Writer writer;
    private TimeoutMatch defaultTimeoutMatch = new TimeoutMatch(DEFAULT_TIMEOUT);
    StringBuffer buffer = new StringBuffer(256);
    boolean eofFound = false;
    boolean finished = false;
    Logger logger = Logger.getLogger(Expect4jImpl.class.getName());

    public Expect4jImpl(Reader reader, Writer writer) {
        this.reader = reader;
        this.writer = writer;
        init();
    }

    private void init() {
        Thread consumerThread = new Thread(this);
        consumerThread.start();
    }


    @Override
    public void send(String str) throws IOException {
        System.out.println("Sending >>> "+ str);
        writer.write(str);
        writer.flush();
    }

    @Override
    public int expect(Match[] matches) {
        if (matches == null || matches.length == 0)
            throw new IllegalArgumentException("Input argument cannot be null or zero length.");
        StopWatch stopWatch = new StopWatch();
        logger.finest("Watch started...");
        stopWatch.start();
        Perl5Matcher matcher = new Perl5Matcher();
        int i;
        while (true) {
            synchronized (this) {
                boolean hasMatch = false;
                ExpectContextImpl expectContext = null;
                for (i=0; i < matches.length; i++) {
                    if (matches[i] instanceof RegExpMatch){
                        RegExpMatch regExpMatch = (RegExpMatch) matches[i];
                        logger.finest("Checking match No: "+i+", "+regExpMatch.toString());
                        String input = buffer.toString();
                        logger.finest("Checking input for match: "+input);
                        if (matcher.contains(input, regExpMatch.getPattern())) {
                            MatchResult result = matcher.getMatch();
                            buffer = new StringBuffer();
                            buffer.append(input.substring(result.end(0)));
                            logger.finest("Matched! Invoking match closure...");
                            expectContext = invokeClosure(regExpMatch, input, result);
                            hasMatch = true;
                            break;
                        }
                    } else if (matches[i] instanceof EofMatch){
                        logger.finest("Checking match No: " + i + ", EofMatch");
                        if (eofFound) {
                            logger.finest("EOF found! Invoking match closure...");
                            EofMatch eofMatch = (EofMatch) matches[i];
                            expectContext = invokeClosure(eofMatch, buffer.toString(), null);
                            hasMatch = true;
                            break;
                        }
                    }
                }
                if (!hasMatch){
                    TimeoutMatch timeoutMatch = findTimeoutMatch(matches);
                    long deltaTime = timeoutMatch.getTimeout() - stopWatch.getTime();
                    logger.finest("First pass no match found. Delta time="+deltaTime);
                    if (deltaTime < 0) {
                        if (timeoutMatch.getClosure() == null) {
                            throw new RuntimeException("Expect timeouted, while expecting: "+
                                    matchesToDump(matches) + " input buffer:"+buffer.toString());
                        }
                        logger.finest("Timeout exceeded. Invoking timeout closure");
                        expectContext = invokeClosure(timeoutMatch, buffer.toString(), null);
                        logger.finest("exp_continue: "+expectContext.isExpContinue() +
                                ", reset_timer: "+expectContext.isResetTimer());
                        if (expectContext.isExpContinue() && expectContext.isResetTimer()) {
                            logger.finest("Resetting stopWatch");
                            stopWatch.reset();
                        } else {
                            logger.finest("Exit expect method due to timeout");
                            break;
                        }
                    } else {
                        logger.finest("wait for input for "+deltaTime+" ms");
                        waitForInput(deltaTime);
                    }
                } else {
                    logger.finest("First pass no match found. ");
                    logger.finest("exp_continue: "+expectContext.isExpContinue()+
                            ", reset_timer: "+expectContext.isResetTimer());
                    if (!expectContext.isExpContinue()) {
                        logger.finest("Exit expect method due to exp_continue=false");
                        break;
                    } else {
                        if (expectContext.isResetTimer()) {
                            logger.finest("Resetting stopWatch");
                            stopWatch.reset();
                        }
                        logger.finest("continue expect method due to exp_continue=true");
                    }
                }
            }
        }
        return i;
    }

    private synchronized void waitForInput(Long timeout) {
        try {
            wait(timeout);
        } catch (InterruptedException e) { throw new RuntimeException(e); }
    }

    private TimeoutMatch findTimeoutMatch(Match[] matches){
        for (Match match : matches) {
            if (match instanceof TimeoutMatch) {
                return (TimeoutMatch) match;
            }
        }
        return defaultTimeoutMatch;
    }

    private ExpectContextImpl invokeClosure(Match match, String input, MatchResult result) {
        ExpectContextImpl expectContext;
        try {
            expectContext = new ExpectContextImpl(result, input);
            Closure closure = match.getClosure();
            if (closure != null) {
                closure.run(expectContext);
            } else {
                logger.finest("No closure defined for this match. Skipping call it");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return expectContext;
    }


    @Override
    public void setTimeout(TimeoutMatch timeoutMatch) {
        if (timeoutMatch == null) {
            throw new IllegalArgumentException("Default timeout cannot be null.");
        }
        this.defaultTimeoutMatch = timeoutMatch;
    }

    @Override
    public void run() {
        try {
            while (!finished) {
                char cs[] = new char[256];
                int size = reader.read(cs);
                synchronized (this) {
                    if (size != -1) {
                        buffer.append(cs, 0, size);
                    } else {
                        eofFound = true;
                        break;
                    }
                    notifyAll();
                }
            }
        } catch (IOException e) {
            logger.log(Level.FINEST,"IO Error in run method",e);
        }
    }
    public void close(){
        finished = true;

    }

    private String matchesToDump(Match[] matches){
        StringBuilder sb = new StringBuilder();
        for (Match match : matches) {
            sb.append(match.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}

package net.itransformers.expect4java.impl;

import net.itransformers.expect4java.Expect4j;
import net.itransformers.expect4java.ExpectContext;
import net.itransformers.expect4java.matches.Match;
import net.itransformers.expect4java.matches.RegExpMatch;
import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Perl5Matcher;

import java.io.*;


public class Expect4jImpl implements Expect4j, Runnable{

    private final Reader reader;
    private final Writer writer;
    ExpectContext state;
    private Thread consumerThread;
    private long timeout = 1000;
    StringBuffer buffer = new StringBuffer(256);

    public Expect4jImpl(Reader reader, Writer writer) {
        this.reader = reader;
        this.writer = writer;
        init();
    }

    private void init() {
        consumerThread = new Thread(this);
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
        Perl5Matcher matcher = new Perl5Matcher();
        int i;
            while (true) {
                synchronized (this) {
                    boolean hasMatch = false;
                    boolean expContinue = false;
                    for (i=0; i < matches.length; i++) {
                        if (matches[i] instanceof RegExpMatch){
                            RegExpMatch regExpMatch = (RegExpMatch) matches[i];
                            String input = buffer.toString();
                            if (matcher.contains(input, regExpMatch.getPattern())) {
                                MatchResult result = matcher.getMatch();
                                hasMatch = true;

                                try {
                                    buffer = new StringBuffer();
                                    buffer.append(input.substring(result.end(0)));
                                    ExpectContextImpl expectContext = new ExpectContextImpl(result, input);
                                    regExpMatch.getClosure().run(expectContext);
                                    expContinue = expectContext.isExpContinue();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    break;
                                }
                                break;
                            }
                        }
                    }
                    if (!hasMatch){
                        try {
                            wait(timeout);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                    } else {
                        if (!expContinue) {
                            break;
                        }
                    }
            }
        }
        return i;
    }


    @Override
    public void setDefaultTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public void run() {
        try {
            while (true) {
                char cs[] = new char[256];
                int size = reader.read(cs);
                synchronized (this) {
                    if (size != -1) {
                        buffer.append(cs, 0, size);
                    } else {
                        break;
                    }
                    notifyAll();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

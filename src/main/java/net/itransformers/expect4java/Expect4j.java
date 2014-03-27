package net.itransformers.expect4java;


import net.itransformers.expect4java.matches.Match;

import java.io.*;

public interface Expect4j {
    void send(String str) throws IOException;
    int expect(Match[] matches);
    void setDefaultTimeout(long timeout);
}

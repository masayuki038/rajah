package net.wrap_trap.rajah.protocol;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class RedisProtocolReader {

    private StringTokenizer framer;

    public RedisProtocolReader(String in) {
        framer = new StringTokenizer(in, "\r\n");
    }

    public Request buildClientRequest() throws RedisProtocolReadException {

        int line = 1;
        String first = readLine(); // the number of an argument;
        if (first == null) {
            throw new RedisProtocolReadException("First line must not null", first, line, 1);
        }
        if (!first.startsWith("*")) {
            throw new RedisProtocolReadException("First line must start with '*'", first, line, 1);
        }

        int num;
        try {
            num = Integer.parseInt(first.substring(1));
        } catch (NumberFormatException e) {
            throw new RedisProtocolReadException("First line must indicate the number of arguments", first, line, 2);
        }

        String[] args = new String[num];
        for (int i = 0; i < num; i++) {
            line++;
            String size = readLine();
            if (size == null) {
                throw new RedisProtocolReadException("The bytes of argument line must not null", first, line, 1);
            }
            if (!size.startsWith("$")) {
                throw new RedisProtocolReadException("The bytes of argument line must start with '$'", size, line, 1);
            }
            line++;
            String value = readLine();
            if (value == null) {
                throw new RedisProtocolReadException("The value of argument line must not null", first, line, 1);
            }
            args[i] = value;
        }

        return new Request(args);
    }

    protected String readLine() {
        try {
            return framer.nextToken();
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}

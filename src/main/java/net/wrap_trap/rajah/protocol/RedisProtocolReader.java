package net.wrap_trap.rajah.protocol;

import java.io.UnsupportedEncodingException;

import net.wrap_trap.rajah.Client;

import org.jboss.netty.buffer.ChannelBuffer;

import com.google.common.base.Preconditions;

public class RedisProtocolReader {

    private byte[] buf;
    private int mark;
    private int position;
    private int limit;

    private static final String CHARSET = "UTF-8";

    public RedisProtocolReader(ChannelBuffer channelBuffer) {
        this.buf = channelBuffer.array();
        this.mark = -1;
        this.position = 0;
        this.limit = buf.length - 1;
    }

    public Client buildClientRequest() throws RedisProtocolReadException {

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

        return new Client(args);
    }

    protected String readLine() {
        mark = position;
        while (true) {
            if (position > limit) {
                if (position > mark) {
                    return buildString(buf, mark, position);
                } else {
                    return null;
                }
            }
            byte b = buf[position++];
            if (b == '\r') {
                if (position > limit) {
                    return buildString(buf, mark, position);
                }
                byte c = buf[position++];
                if (c == '\n') {
                    String ret = buildString(buf, mark, position - 2);
                    if ((ret != null) || (position > limit)) {
                        return ret;
                    } else {
                        // ignoring linefeed and continue
                        mark = position;
                    }
                }
            }
        }
    }

    protected static String buildString(byte[] buf, int from, int to) {
        Preconditions.checkArgument((from <= to), "from <= to");
        int span = to - from;
        if (span == 0) {
            return null;
        }
        byte[] dest = new byte[span];
        System.arraycopy(buf, from, dest, 0, dest.length);
        try {
            return new String(dest, CHARSET);
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
}

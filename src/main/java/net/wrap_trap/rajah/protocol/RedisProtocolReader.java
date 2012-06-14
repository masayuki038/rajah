package net.wrap_trap.rajah.protocol;

import java.io.UnsupportedEncodingException;

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

    public String readLine() {
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

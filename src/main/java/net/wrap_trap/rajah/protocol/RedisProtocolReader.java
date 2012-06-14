package net.wrap_trap.rajah.protocol;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import org.jboss.netty.buffer.ChannelBuffer;

public class RedisProtocolReader {

    private ByteBuffer buf;
    private static final String CHARSET = "UTF-8";

    public RedisProtocolReader(ChannelBuffer channelBuffer) {
        this.buf = channelBuffer.toByteBuffer();
    }

    public String readLine() {
        try {
            while (true) {
                if (!buf.hasRemaining()) {
                    return buildString(buf);
                }
                byte b = buf.get();
                if (b == '\r') {
                    if (!buf.hasRemaining()) {
                        return buildString(buf);
                    }
                    byte c = buf.get();
                    if (c == '\n') {
                        buf.position(buf.position() - 2);
                        String ret = buildString(buf);
                        buf.position(buf.position() + 2);
                        return ret;
                    }
                }
            }
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected static String buildString(ByteBuffer buf) throws UnsupportedEncodingException {
        return new String(buf.compact().array(), CHARSET);
    }
}

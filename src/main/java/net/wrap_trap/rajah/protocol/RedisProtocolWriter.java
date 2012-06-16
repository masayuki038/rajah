package net.wrap_trap.rajah.protocol;

import java.io.UnsupportedEncodingException;

import org.jboss.netty.buffer.ChannelBuffer;

public class RedisProtocolWriter {

    private ChannelBuffer channelBuffer;

    private static final String CHARSET = "UTF-8";
    private static final String LT = "\r\n";

    public RedisProtocolWriter(ChannelBuffer channelBuffer) {
        this.channelBuffer = channelBuffer;
    }

    public void writeStatusReply(String status) throws RedisProtocolWriteException {
        try {
            channelBuffer.writeBytes(("+" + status).getBytes(CHARSET));
        } catch (UnsupportedEncodingException ex) {
            throw new RedisProtocolWriteException(ex);
        }
    }

    public void writeBulkReply(String value) throws RedisProtocolWriteException {
        try {
            String ret = "$" + Integer.toString(value.length()) + LT + value + LT;
            channelBuffer.writeBytes(ret.getBytes(CHARSET));
        } catch (UnsupportedEncodingException ex) {
            throw new RedisProtocolWriteException(ex);
        }
    }
}

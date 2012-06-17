package net.wrap_trap.rajah.protocol;

import org.jboss.netty.channel.Channel;

public class RedisProtocolWriter {

    private Channel channel;

    private static final String CHARSET = "UTF-8";
    private static final String LT = "\r\n";

    public RedisProtocolWriter(Channel channel) {
        this.channel = channel;
    }

    public void writeStatusReply(String status) throws RedisProtocolWriteException {
        try {
            channel.write("+" + status + LT);
        } catch (Exception ex) {
            throw new RedisProtocolWriteException(ex);
        }
    }

    public void writeBulkReply(String value) throws RedisProtocolWriteException {
        try {
            String ret = "$" + Integer.toString(value.length()) + LT + value + LT;
            channel.write(ret);
        } catch (Exception ex) {
            throw new RedisProtocolWriteException(ex);
        }
    }
}

package net.wrap_trap.rajah;

import net.wrap_trap.rajah.protocol.RedisProtocolWriteException;

import org.jboss.netty.channel.Channel;

public class BulkReplies implements Reply {

    private String value;

    public BulkReplies(String value) {
        super();
        this.value = value;
    }

    public void write(Channel channel) throws RedisProtocolWriteException {
        try {
            String ret = "$" + Integer.toString(value.length()) + LT + value + LT;
            channel.write(ret);
        } catch (Exception ex) {
            throw new RedisProtocolWriteException(ex);
        }
    }
}

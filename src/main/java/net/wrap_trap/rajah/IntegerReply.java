package net.wrap_trap.rajah;

import net.wrap_trap.rajah.protocol.RedisProtocolWriteException;
import net.wrap_trap.rajah.protocol.RedisProtocolWriter;

import org.jboss.netty.channel.Channel;

public class IntegerReply implements Reply {

    private Integer value;

    public IntegerReply(Integer value) {
        super();
        this.value = value;
    }

    public void write(Channel channel) throws RedisProtocolWriteException {
        RedisProtocolWriter writer = new RedisProtocolWriter(channel);
        writer.writeIntegerReply(value);
    }
}

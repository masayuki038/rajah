package net.wrap_trap.rajah;

import net.wrap_trap.rajah.protocol.RedisProtocolWriteException;
import net.wrap_trap.rajah.protocol.RedisProtocolWriter;

import org.jboss.netty.buffer.ChannelBuffer;

public class OkReply implements Reply {

    public void write(ChannelBuffer channelBuffer) throws RedisProtocolWriteException {
        RedisProtocolWriter writer = new RedisProtocolWriter(channelBuffer);
        writer.writeStatusReply("OK");
    }
}

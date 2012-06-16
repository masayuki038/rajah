package net.wrap_trap.rajah;

import net.wrap_trap.rajah.protocol.RedisProtocolWriteException;

import org.jboss.netty.buffer.ChannelBuffer;

public interface Reply {

    void write(ChannelBuffer channelBuffer) throws RedisProtocolWriteException;

}

package net.wrap_trap.rajah;

import net.wrap_trap.rajah.protocol.RedisProtocolWriteException;

import org.jboss.netty.channel.Channel;

public interface Reply {

    void write(Channel channel) throws RedisProtocolWriteException;

}

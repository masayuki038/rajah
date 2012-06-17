package net.wrap_trap.rajah;

import net.wrap_trap.rajah.protocol.RedisProtocolWriteException;

import org.jboss.netty.channel.Channel;

public interface Reply {

    public static final String LT = "\r\n";

    void write(Channel channel) throws RedisProtocolWriteException;

}

package net.wrap_trap.rajah.protocol;


import org.jboss.netty.channel.Channel;

public interface Reply {

    public static final String LT = "\r\n";

    void write(Channel channel) throws RedisProtocolWriteException;

}

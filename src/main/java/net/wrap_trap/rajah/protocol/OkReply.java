package net.wrap_trap.rajah.protocol;


import org.jboss.netty.channel.Channel;

public class OkReply implements Reply {

    public void write(Channel channel) throws RedisProtocolWriteException {
        try {
            channel.write("+OK" + LT);
        } catch (Exception ex) {

        }
    }
}

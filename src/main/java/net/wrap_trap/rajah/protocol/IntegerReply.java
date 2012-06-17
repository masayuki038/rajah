package net.wrap_trap.rajah.protocol;


import org.jboss.netty.channel.Channel;

public class IntegerReply implements Reply {

    private Integer value;

    public IntegerReply(Integer value) {
        super();
        this.value = value;
    }

    public void write(Channel channel) throws RedisProtocolWriteException {
        try {
            String ret = ":" + value + LT;
            channel.write(ret);
        } catch (Exception ex) {
            throw new RedisProtocolWriteException(ex);
        }
    }
}

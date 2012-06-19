package net.wrap_trap.rajah.protocol;

import java.util.ArrayList;
import java.util.List;

import org.jboss.netty.channel.Channel;

public class MultiBulkReplies implements Reply {

    private List<BulkReplies> replies;

    public MultiBulkReplies() {
        super();
        replies = new ArrayList<BulkReplies>();
    }

    public void add(String value) {
        replies.add(new BulkReplies(value));
    }

    public void write(Channel channel) throws RedisProtocolWriteException {
        try {
            channel.write("*" + replies.size() + LT);
            for (BulkReplies r : replies) {
                r.write(channel);
            }
        } catch (Exception ex) {
            throw new RedisProtocolWriteException(ex);
        }
    }

}

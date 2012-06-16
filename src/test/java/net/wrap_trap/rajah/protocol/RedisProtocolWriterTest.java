package net.wrap_trap.rajah.protocol;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.io.UnsupportedEncodingException;

import net.wrap_trap.rajah.BulkReplies;
import net.wrap_trap.rajah.OkReply;
import net.wrap_trap.rajah.Reply;

import org.jboss.netty.buffer.ChannelBuffer;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class RedisProtocolWriterTest {

    @Test
    public void testStatusReply() throws RedisProtocolWriteException {
        StringBuffer ret = new StringBuffer();
        ChannelBuffer cb = createChannelBuffer(ret);
        Reply reply = new OkReply();
        reply.write(cb);
        assertThat(ret.toString(), is("+OK"));
    }

    @Test
    public void testBulkReplies() throws RedisProtocolWriteException {
        StringBuffer ret = new StringBuffer();
        ChannelBuffer cb = createChannelBuffer(ret);
        Reply reply = new BulkReplies("bar");
        reply.write(cb);
        assertThat(ret.toString(), is("$3\r\nbar\r\n"));
    }

    protected ChannelBuffer createChannelBuffer(final StringBuffer sb) {
        ChannelBuffer cb = mock(ChannelBuffer.class);
        try {
            doAnswer(new Answer<Void>() {
                public Void answer(InvocationOnMock invocation) {
                    Object[] args = invocation.getArguments();
                    try {
                        sb.append(new String((byte[]) args[0], "UTF-8"));
                        return null;
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).when(cb).writeBytes(any(byte[].class));
            return cb;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

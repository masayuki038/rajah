package net.wrap_trap.rajah.channel;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import net.wrap_trap.rajah.protocol.Reply;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.MessageEvent;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class CommandHandlerTest {

    @Test
    public void testSetGet() {
        CommandChannelHandler h = new CommandChannelHandler();
        set(h, "*3", "$3", "SET", "$3", "foo", "$3", "bar");
        get(h, "bar", "*2", "$3", "GET", "$3", "foo");
    }

    @Test
    public void testExists() {
        CommandChannelHandler h = new CommandChannelHandler();
        exists(h, 0, "*2", "$6", "EXISTS", "$3", "foo");
        set(h, "*3", "$3", "SET", "$3", "foo", "$3", "bar");
        exists(h, 1, "*2", "$6", "EXISTS", "$3", "foo");
    }

    @Test
    public void testDel() {
        CommandChannelHandler h = new CommandChannelHandler();
        set(h, "*3", "$3", "SET", "$3", "foo", "$3", "bar");
        set(h, "*3", "$3", "SET", "$4", "hoge", "$3", "hogehoge");
        del(h, 2, "*3", "$3", "DEL", "$3", "foo", "$4", "hoge");
        get(h, null, "*2", "$3", "GET", "$3", "foo");
    }

    @Test
    public void testMget() {
        CommandChannelHandler h = new CommandChannelHandler();
        set(h, "*3", "$3", "SET", "$3", "foo", "$3", "bar");
        set(h, "*3", "$3", "SET", "$3", "hoge", "$3", "hogehoge");
        String[] expected = new String[] { "bar", null, "hogehoge" };
        mget(h, expected, "*4", "$3", "MGET", "$3", "foo", "$11", "nonexisting", "$4", "hoge");
    }

    protected void set(CommandChannelHandler h, String... args) {
        StringBuffer setRet = new StringBuffer();
        String setIn = createRequest(args);

        MessageEvent e = createMessageEvent(setIn, setRet);
        h.messageReceived(null, e);
        assertThat(setRet.toString(), is("+OK" + Reply.LT));
    }

    protected void get(CommandChannelHandler h, String expected, String... args) {
        StringBuffer getRet = new StringBuffer();
        String getIn = createRequest(args);
        MessageEvent e2 = createMessageEvent(getIn, getRet);
        h.messageReceived(null, e2);
        if (expected != null) {
            assertThat(getRet.toString(), is("$" + expected.length() + Reply.LT + expected + Reply.LT));
        } else {
            assertThat(getRet.toString(), is("$-1" + Reply.LT));
        }
    }

    protected void exists(CommandChannelHandler h, int expected, String... args) {
        StringBuffer existsRet = new StringBuffer();
        String existsIn = createRequest(args);
        MessageEvent e = createMessageEvent(existsIn, existsRet);
        h.messageReceived(null, e);
        assertThat(existsRet.toString(), is(":" + expected + Reply.LT));
    }

    protected void del(CommandChannelHandler h, int expected, String... args) {
        StringBuffer delRet = new StringBuffer();
        String delIn = createRequest(args);
        MessageEvent e3 = createMessageEvent(delIn, delRet);
        h.messageReceived(null, e3);
        assertThat(delRet.toString(), is(":" + expected + Reply.LT));
    }

    protected void mget(CommandChannelHandler h, String[] expected, String... args) {
        StringBuffer mgetRet = new StringBuffer();
        String mgetIn = createRequest(args);
        MessageEvent e2 = createMessageEvent(mgetIn, mgetRet);
        h.messageReceived(null, e2);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < expected.length; i++) {
            if (expected[i] != null) {
                sb.append("$" + expected[i].length() + Reply.LT + expected[i] + Reply.LT);
            } else {
                sb.append("$-1" + Reply.LT);
            }
        }
        sb.insert(0, "*" + expected.length + Reply.LT);
        assertThat(mgetRet.toString(), is(sb.toString()));
    }

    protected String createRequest(String... args) {
        return StringUtils.join(args, Reply.LT) + Reply.LT;
    }

    protected MessageEvent createMessageEvent(String in, final StringBuffer sb) {
        Channel channel = createChannel(sb);
        MessageEvent messageEvent = mock(MessageEvent.class);
        when(messageEvent.getMessage()).thenReturn(in);
        when(messageEvent.getChannel()).thenReturn(channel);
        return messageEvent;
    }

    protected Channel createChannel(final StringBuffer sb) {
        Channel channel = mock(Channel.class);
        try {
            doAnswer(new Answer<Void>() {
                public Void answer(InvocationOnMock invocation) {
                    Object[] args = invocation.getArguments();
                    sb.append(args[0]);
                    return null;
                }
            }).when(channel).write(any(String.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return channel;
    }
}

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
        StringBuffer setRet = new StringBuffer();
        String setIn = createRequest("*3", "$3", "SET", "$3", "foo", "$3", "bar");
        CommandChannelHandler h = new CommandChannelHandler();

        MessageEvent e = createMessageEvent(setIn, setRet);
        h.messageReceived(null, e);
        assertThat(setRet.toString(), is("+OK" + Reply.LT));

        StringBuffer getRet = new StringBuffer();
        String getIn = createRequest("*2", "$3", "GET", "$3", "foo");
        MessageEvent e2 = createMessageEvent(getIn, getRet);
        h.messageReceived(null, e2);
        assertThat(getRet.toString(), is("$3" + Reply.LT + "bar" + Reply.LT));
    }

    @Test
    public void testExists() {
        CommandChannelHandler h = new CommandChannelHandler();

        StringBuffer existsRet = new StringBuffer();
        String existsIn = createRequest("*2", "$6", "EXISTS", "$3", "foo");
        MessageEvent e = createMessageEvent(existsIn, existsRet);
        h.messageReceived(null, e);
        assertThat(existsRet.toString(), is(":0" + Reply.LT));

        StringBuffer setRet = new StringBuffer();
        String setIn = createRequest("*3", "$3", "SET", "$3", "foo", "$3", "bar");
        MessageEvent e2 = createMessageEvent(setIn, setRet);
        h.messageReceived(null, e2);
        assertThat(setRet.toString(), is("+OK" + Reply.LT));

        StringBuffer existsRet2 = new StringBuffer();
        String existsIn2 = createRequest("*2", "$6", "EXISTS", "$3", "foo");
        MessageEvent e3 = createMessageEvent(existsIn2, existsRet2);
        h.messageReceived(null, e3);
        assertThat(existsRet2.toString(), is(":1" + Reply.LT));
    }

    @Test
    public void testDel() {
        CommandChannelHandler h = new CommandChannelHandler();

        StringBuffer setRet = new StringBuffer();
        String setIn = createRequest("*3", "$3", "SET", "$3", "foo", "$3", "bar");
        MessageEvent e = createMessageEvent(setIn, setRet);
        h.messageReceived(null, e);
        assertThat(setRet.toString(), is("+OK" + Reply.LT));

        StringBuffer setRet2 = new StringBuffer();
        String setIn2 = createRequest("*3", "$3", "SET", "$3", "hoge", "$3", "hogehoge");
        MessageEvent e2 = createMessageEvent(setIn2, setRet2);
        h.messageReceived(null, e2);
        assertThat(setRet2.toString(), is("+OK" + Reply.LT));

        StringBuffer delRet = new StringBuffer();
        String delIn = createRequest("*3", "$3", "DEL", "$3", "foo", "$4", "hoge");
        MessageEvent e3 = createMessageEvent(delIn, delRet);
        h.messageReceived(null, e3);
        assertThat(delRet.toString(), is(":2" + Reply.LT));

        StringBuffer getRet = new StringBuffer();
        String getIn = createRequest("*2", "$3", "GET", "$3", "foo");
        MessageEvent e4 = createMessageEvent(getIn, getRet);
        h.messageReceived(null, e4);
        assertThat(getRet.toString(), is("$-1" + Reply.LT));
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

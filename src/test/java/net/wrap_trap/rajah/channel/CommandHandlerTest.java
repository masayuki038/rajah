package net.wrap_trap.rajah.channel;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import net.wrap_trap.rajah.Database;
import net.wrap_trap.rajah.protocol.Reply;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.MessageEvent;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class CommandHandlerTest {

	@Test
	public void testExpire() throws InterruptedException {
		final CommandChannelHandler h = new CommandChannelHandler(new Database());
		expire(h, 0, "foo", "10");
		set(h, "foo", "bar");
		expire(h, 1, "foo", "10");
		
        final boolean[] results = new boolean[2];
        
        Timer timer = new Timer("a few second later", false);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                get(h, "bar", "foo");
                results[0] = true;
            }
        }, 9000L);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                get(h, null, "foo");
                results[1] = true;
            }
        }, 10000L);
        TimeUnit.SECONDS.sleep(11);
        
        assertThat(results[0], is(true));
        assertThat(results[1], is(true));
	}
	
    @Test
    public void testSetGet() {
        CommandChannelHandler h = new CommandChannelHandler(new Database());
        set(h, "foo", "bar");
        get(h, "bar", "foo");
    }

    @Test
    public void testExists() {
        CommandChannelHandler h = new CommandChannelHandler(new Database());
        exists(h, 0, "foo");
        set(h, "foo", "bar");
        exists(h, 1, "foo");
    }

    @Test
    public void testDel() {
        CommandChannelHandler h = new CommandChannelHandler(new Database());
        set(h, "foo", "bar");
        set(h, "hoge", "hogehoge");
        del(h, 2, "foo", "hoge");
        get(h, null, "foo");
    }

    @Test
    public void testMget() {
        CommandChannelHandler h = new CommandChannelHandler(new Database());
        set(h, "foo", "bar");
        set(h, "hoge", "hogehoge");
        String[] expected = new String[] { "bar", null, "hogehoge" };
        mget(h, expected, "foo", "nonexisting", "hoge");
    }

    @Test
    public void testMset() {
        CommandChannelHandler h = new CommandChannelHandler(new Database());
        mset(h, "foo", "bar", "hoge", "hogehoge");
        String[] expected = new String[] { "bar", null, "hogehoge" };
        mget(h, expected, "foo", "nonexisting", "hoge");
    }

    @Test
    public void testSetExGet() throws InterruptedException {
        final CommandChannelHandler h = new CommandChannelHandler(new Database());
        final boolean[] results = new boolean[2];

        setEx(h, "foo", "10", "bar");
        get(h, "bar", "foo");

        Timer timer = new Timer("a few second later", false);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                get(h, "bar", "foo");
                results[0] = true;
            }
        }, 9000L);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                get(h, null, "foo");
                results[1] = true;
            }
        }, 10000L);
        TimeUnit.SECONDS.sleep(11);

        assertThat(results[0], is(true));
        assertThat(results[1], is(true));
    }

    @Test
    public void testSetExMGet() throws InterruptedException {
        final CommandChannelHandler h = new CommandChannelHandler(new Database());
        final boolean[] results = new boolean[3];

        setEx(h, "foo", "10", "bar");
        setEx(h, "hoge", "5", "hogehoge");
        mget(h, new String[] { "bar", "hogehoge" }, "foo", "hoge");

        Timer timer = new Timer("a few second later", false);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mget(h, new String[] { "bar", "hogehoge" }, "foo", "hoge");
                results[0] = true;
            }
        }, 4500L);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mget(h, new String[] { "bar", null }, "foo", "hoge");
                results[1] = true;
            }
        }, 9500L);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mget(h, new String[] { null, null }, "foo", "hoge");
                results[2] = true;
            }
        }, 10500L);
        TimeUnit.SECONDS.sleep(11);

        assertThat(results[0], is(true));
        assertThat(results[1], is(true));
        assertThat(results[2], is(true));
    }

    @Test
    public void testSetExExists() throws InterruptedException {
        final CommandChannelHandler h = new CommandChannelHandler(new Database());
        final boolean[] results = new boolean[3];

        setEx(h, "foo", "10", "bar");
        setEx(h, "hoge", "5", "hogehoge");
        exists(h, 1, "foo");
        exists(h, 1, "hoge");

        Timer timer = new Timer("a few second later", false);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                exists(h, 1, "foo");
                exists(h, 1, "hoge");
                results[0] = true;
            }
        }, 4500L);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                exists(h, 1, "foo");
                exists(h, 0, "hoge");
                results[1] = true;
            }
        }, 9500L);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                exists(h, 0, "foo");
                exists(h, 0, "hoge");
                results[2] = true;
            }
        }, 10500L);
        TimeUnit.SECONDS.sleep(11);

        assertThat(results[0], is(true));
        assertThat(results[1], is(true));
        assertThat(results[2], is(true));
    }

    protected void set(CommandChannelHandler h, String... args) {
        StringBuffer setRet = new StringBuffer();
        String setIn = createRequest(createCommand("SET", args));

        MessageEvent e = createMessageEvent(setIn, setRet);
        h.messageReceived(null, e);
        assertThat(setRet.toString(), is("+OK" + Reply.LT));
    }

    protected void get(CommandChannelHandler h, String expected, String... args) {
        StringBuffer getRet = new StringBuffer();
        String getIn = createRequest(createCommand("GET", args));
        MessageEvent e2 = createMessageEvent(getIn, getRet);
        h.messageReceived(null, e2);
        if (expected != null) {
            assertThat(getRet.toString(), is("$" + expected.length() + Reply.LT + expected + Reply.LT));
        } else {
            assertThat(getRet.toString(), is("$-1" + Reply.LT));
        }
    }

    protected void exists(CommandChannelHandler h, int expected, String key) {
        StringBuffer existsRet = new StringBuffer();
        String existsIn = createRequest(createCommand("EXISTS", key));
        MessageEvent e = createMessageEvent(existsIn, existsRet);
        h.messageReceived(null, e);
        assertThat(existsRet.toString(), is(":" + expected + Reply.LT));
    }

    protected void del(CommandChannelHandler h, int expected, String... args) {
        StringBuffer delRet = new StringBuffer();
        String delIn = createRequest(createCommand("DEL", args));
        MessageEvent e3 = createMessageEvent(delIn, delRet);
        h.messageReceived(null, e3);
        assertThat(delRet.toString(), is(":" + expected + Reply.LT));
    }

    protected void mget(CommandChannelHandler h, String[] expected, String... args) {
        StringBuffer mgetRet = new StringBuffer();
        String mgetIn = createRequest(createCommand("MGET", args));
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

    protected void mset(CommandChannelHandler h, String... args) {
        StringBuffer msetRet = new StringBuffer();
        String msetIn = createRequest(createCommand("MSET", args));
        MessageEvent e2 = createMessageEvent(msetIn, msetRet);
        h.messageReceived(null, e2);
        assertThat(msetRet.toString(), is("+OK" + Reply.LT));
    }

    protected void setEx(CommandChannelHandler h, String... args) {
        StringBuffer setRet = new StringBuffer();
        String setIn = createRequest(createCommand("SETEX", args));

        MessageEvent e = createMessageEvent(setIn, setRet);
        h.messageReceived(null, e);
        assertThat(setRet.toString(), is("+OK" + Reply.LT));
    }
    
    protected void expire(CommandChannelHandler h, int expected, String... args) {
        StringBuffer expireRet = new StringBuffer();
        String expireIn = createRequest(createCommand("EXPIRE", args));

        MessageEvent e = createMessageEvent(expireIn, expireRet);
        h.messageReceived(null, e);
        assertThat(expireRet.toString(), is(":" + expected + Reply.LT));
    }


    protected String[] createCommand(String command, String... args) {
        int len = (args.length + 1) * 2 + 1;
        String[] expanded = new String[len];
        int i = 0;
        expanded[i++] = "*" + ((len - 1) / 2);
        expanded[i++] = "$" + command.length();
        expanded[i++] = command;
        for (int j = 0; j < args.length; j++) {
            expanded[i++] = "$" + args[j].length();
            expanded[i++] = args[j];
        }
        return expanded;
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

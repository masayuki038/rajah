package net.wrap_trap.rajah.channel;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import net.wrap_trap.rajah.Database;
import net.wrap_trap.rajah.protocol.Reply;
import net.wrap_trap.rajah.test.RajahResponse;
import net.wrap_trap.rajah.test.utils.AssertTask;

import org.apache.commons.lang3.StringUtils;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.MessageEvent;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class CommandHandlerTest {

    @Test
    public void testSetGet() {
        CommandChannelHandler h = new CommandChannelHandler(new Database());
        set(h, "foo", "bar").isOK();
        get(h, "foo").is("bar");
    }

    @Test
    public void testExists() {
        CommandChannelHandler h = new CommandChannelHandler(new Database());
        exists(h, "foo").is(0);
        set(h, "foo", "bar").isOK();
        exists(h, "foo").is(1);
    }

    @Test
    public void testDel() {
        CommandChannelHandler h = new CommandChannelHandler(new Database());
        set(h, "foo", "bar").isOK();
        set(h, "hoge", "hogehoge").isOK();
        del(h, "foo", "hoge").is(2);
        get(h, "foo").is((String)null);
    }

    @Test
    public void testMget() {
        CommandChannelHandler h = new CommandChannelHandler(new Database());
        set(h, "foo", "bar").isOK();
        set(h, "hoge", "hogehoge").isOK();
        mget(h, "foo", "nonexisting", "hoge").is(new String[]{ "bar", null, "hogehoge" });
    }

    @Test
    public void testMset() {
        CommandChannelHandler h = new CommandChannelHandler(new Database());
        mset(h, "foo", "bar", "hoge", "hogehoge").isOK();
        mget(h, "foo", "nonexisting", "hoge").is(new String[]{ "bar", null, "hogehoge" });
    }

    @Test
    public void testSetExAndGetWithinLiveTime() throws InterruptedException {
        final CommandChannelHandler h = new CommandChannelHandler(new Database());

        setEx(h, "foo", "10", "bar").isOK();
        get(h, "foo").is("bar");
        
        new AssertTask(new Runnable() {
			public void run() {
		        get(h, "foo").is("bar");
			}
        }).assertLater(9000L);
    }
    
    @Test
    public void testSetExAndOverdueGet() throws InterruptedException {
        final CommandChannelHandler h = new CommandChannelHandler(new Database());

        setEx(h, "foo", "10", "bar").isOK();
        
        new AssertTask(new Runnable() {
			public void run() {
		        get(h, "foo").is((String)null);
			}
        }).assertLater(10000L);
    }

    @Test
    public void testSetExAndMGetWithinLiveTime() throws InterruptedException {
        final CommandChannelHandler h = new CommandChannelHandler(new Database());

        setEx(h, "foo", "10", "bar").isOK();
        setEx(h, "hoge", "5", "hogehoge").isOK();
        mget(h, "foo", "hoge").is(new String[] { "bar", "hogehoge" });
        
        new AssertTask(new Runnable() {
			public void run() {
                mget(h, "foo", "hoge").is(new String[] { "bar", "hogehoge" });
			}
        }).assertLater(4500L);
    }
    
    @Test
    public void testSetExAndOverduMGet1() throws InterruptedException {
        final CommandChannelHandler h = new CommandChannelHandler(new Database());

    	setEx(h, "foo", "10", "bar").isOK();
        setEx(h, "hoge", "5", "hogehoge").isOK();
        mget(h, "foo", "hoge").is(new String[] { "bar", "hogehoge" });
        
        new AssertTask(new Runnable() {
			public void run() {
                mget(h, "foo", "hoge").is(new String[] { "bar", null });
			}
        }).assertLater(9500L);
    }
    
    @Test
    public void testSetExAndOverdueMGet2() throws InterruptedException {
        final CommandChannelHandler h = new CommandChannelHandler(new Database());

    	setEx(h, "foo", "10", "bar").isOK();
    	setEx(h, "hoge", "5", "hogehoge").isOK();
        mget(h, "foo", "hoge").is(new String[] { "bar", "hogehoge" });
        
        new AssertTask(new Runnable() {
			public void run() {
                mget(h, "foo", "hoge").is( new String[] { null, null });
			}
        }).assertLater(10500L);
    }

    @Test
    public void testSetExAndExistsWithinLiveTime() throws InterruptedException {
        final CommandChannelHandler h = new CommandChannelHandler(new Database());

        setEx(h, "foo", "10", "bar").isOK();
        setEx(h, "hoge", "5", "hogehoge").isOK();
        exists(h, "foo").is(1);
        exists(h, "hoge").is(1);

        new AssertTask(new Runnable() {
			public void run() {
                exists(h, "foo").is(1);
                exists(h, "hoge").is(1);
			}
        }).assertLater(4500L);
    }
    
    @Test
    public void testSetExAndOverdueExists1() throws InterruptedException {
        final CommandChannelHandler h = new CommandChannelHandler(new Database());
    
        setEx(h, "foo", "10", "bar").isOK();
        setEx(h, "hoge", "5", "hogehoge").isOK();
        
        new AssertTask(new Runnable() {
			public void run() {
				exists(h, "foo").is(1);
				exists(h, "hoge").is(0);
			}
        }).assertLater(9500L);
    }
    
    @Test
    public void testSetExAndOverdueExists2() throws InterruptedException {
        final CommandChannelHandler h = new CommandChannelHandler(new Database());
        
        setEx(h, "foo", "10", "bar").isOK();
        setEx(h, "hoge", "5", "hogehoge").isOK();
        
        new AssertTask(new Runnable() {
			public void run() {
                exists(h, "foo").is(0);
                exists(h, "hoge").is(0);
			}
        }).assertLater(10500L);
    }
	
	@Test
	public void testExpireAndGetWithLiveTime() throws InterruptedException {
		final CommandChannelHandler h = new CommandChannelHandler(new Database());

		expire(h, "foo", "10").is(0);
		set(h, "foo", "bar").isOK();
		expire(h, "foo", "10").is(1);
		
        new AssertTask(new Runnable() {
			public void run() {
		        get(h, "foo").is("bar");
			}
        }).assertLater(9000L);
    }
	
	@Test
	public void testExpireAndOverdueGet() throws InterruptedException {
		final CommandChannelHandler h = new CommandChannelHandler(new Database());
		
		set(h, "foo", "bar").isOK();
		expire(h, "foo", "10").is(1);
		
        new AssertTask(new Runnable() {
			public void run() {
		        get(h, "foo").is((String)null);
			}
        }).assertLater(10000L);
	}
	
	@Test
	public void testTtlAndGetWithLiveInTime() throws InterruptedException {
		final CommandChannelHandler h = new CommandChannelHandler(new Database());

		ttl(h, "foo").is(-1);
		
		set(h, "foo", "bar").isOK();
		ttl(h, "foo").is(-1);
		
		setEx(h, "foo", "10", "bar").isOK();

        new AssertTask(new Runnable() {
			public void run() {
				ttl(h, "foo").is(1);
		        get(h, "foo").is("bar");
			}
        }).assertLater(8000L);
    }
	
	@Test
	public void testTtlAndOverdueGet() throws InterruptedException {
		final CommandChannelHandler h = new CommandChannelHandler(new Database());
        
		setEx(h, "foo", "10", "bar").isOK();

        new AssertTask(new Runnable() {
			public void run() {
				ttl(h, "foo").is(-1);
                get(h, "foo").is((String)null);
			}
        }).assertLater(10000L);
	}

	protected RajahResponse set(CommandChannelHandler h, String... args) {
        StringBuffer setOut = new StringBuffer();
        String setIn = createRequest(createCommand("SET", args));

        MessageEvent e = createMessageEvent(setIn, setOut);
        h.messageReceived(null, e);
        return new RajahResponse(setOut.toString());
    }

    protected RajahResponse get(CommandChannelHandler h, String... args) {
        StringBuffer getOut = new StringBuffer();
        String getIn = createRequest(createCommand("GET", args));
        MessageEvent e2 = createMessageEvent(getIn, getOut);
        h.messageReceived(null, e2);
        return new RajahResponse(getOut.toString());
    }

    protected RajahResponse exists(CommandChannelHandler h, String key) {
        StringBuffer existsOut = new StringBuffer();
        String existsIn = createRequest(createCommand("EXISTS", key));
        MessageEvent e = createMessageEvent(existsIn, existsOut);
        h.messageReceived(null, e);
        return new RajahResponse(existsOut.toString());
    }

    protected RajahResponse del(CommandChannelHandler h, String... args) {
        StringBuffer delOut = new StringBuffer();
        String delIn = createRequest(createCommand("DEL", args));
        MessageEvent e3 = createMessageEvent(delIn, delOut);
        h.messageReceived(null, e3);
        return new RajahResponse(delOut.toString());
    }

    protected RajahResponse mget(CommandChannelHandler h, String... args) {
        StringBuffer mgetOut = new StringBuffer();
        String mgetIn = createRequest(createCommand("MGET", args));
        MessageEvent e2 = createMessageEvent(mgetIn, mgetOut);
        h.messageReceived(null, e2);
        return new RajahResponse(mgetOut.toString());
    }

    protected RajahResponse mset(CommandChannelHandler h, String... args) {
        StringBuffer msetOut = new StringBuffer();
        String msetIn = createRequest(createCommand("MSET", args));
        MessageEvent e2 = createMessageEvent(msetIn, msetOut);
        h.messageReceived(null, e2);
        return new RajahResponse(msetOut.toString());
    }

    protected RajahResponse setEx(CommandChannelHandler h, String... args) {
        StringBuffer setOut = new StringBuffer();
        String setIn = createRequest(createCommand("SETEX", args));

        MessageEvent e = createMessageEvent(setIn, setOut);
        h.messageReceived(null, e);
        return new RajahResponse(setOut.toString());
    }
    
    protected RajahResponse expire(CommandChannelHandler h, String... args) {
        StringBuffer expireOut = new StringBuffer();
        String expireIn = createRequest(createCommand("EXPIRE", args));

        MessageEvent e = createMessageEvent(expireIn, expireOut);
        h.messageReceived(null, e);
        return new RajahResponse(expireOut.toString());
    }

    protected RajahResponse ttl(CommandChannelHandler h, String... args) {
        StringBuffer ttlOut = new StringBuffer();
        String ttlIn = createRequest(createCommand("TTL", args));

        MessageEvent e = createMessageEvent(ttlIn, ttlOut);
        h.messageReceived(null, e);
        return new RajahResponse(ttlOut.toString());
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

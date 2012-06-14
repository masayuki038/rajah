package net.wrap_trap.rajah.protocol;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.jboss.netty.buffer.ChannelBuffer;
import org.junit.Test;

public class RedisProtocolReaderTest {

    @Test
    public void testNull() {
        RedisProtocolReader reader = createRedisProtocolReader("");
        assertThat(reader.readLine(), nullValue());
    }

    @Test
    public void testNull2() {
        RedisProtocolReader reader = createRedisProtocolReader("\r\n");
        assertThat(reader.readLine(), nullValue());
    }

    @Test
    public void testCRLFCRLF_A() {
        RedisProtocolReader reader = createRedisProtocolReader("\r\n\r\na");
        assertThat(reader.readLine(), is("a"));
        assertThat(reader.readLine(), nullValue());
    }

    @Test
    public void testGet2LineOfAscii() {
        RedisProtocolReader reader = createRedisProtocolReader("foo\r\nbar");
        assertThat(reader.readLine(), is("foo"));
        assertThat(reader.readLine(), is("bar"));
        assertThat(reader.readLine(), nullValue());
    }

    @Test
    public void testGetLineOfAsciiTerminatedCRLF() {
        RedisProtocolReader reader = createRedisProtocolReader("foo\r\nbar\r\n");
        assertThat(reader.readLine(), is("foo"));
        assertThat(reader.readLine(), is("bar"));
        assertThat(reader.readLine(), nullValue());
    }

    @Test
    public void testGetLineOfAsciiTerminatedCR() {
        RedisProtocolReader reader = createRedisProtocolReader("foo\r\nbar\r");
        assertThat(reader.readLine(), is("foo"));
        assertThat(reader.readLine(), is("bar\r"));
        assertThat(reader.readLine(), nullValue());
    }

    @Test
    public void testGetLineOfAsciiTerminatedLF() {
        RedisProtocolReader reader = createRedisProtocolReader("foo\r\nbar\n");
        assertThat(reader.readLine(), is("foo"));
        assertThat(reader.readLine(), is("bar\n"));
        assertThat(reader.readLine(), nullValue());
    }

    @Test
    public void testGet3LineOfAscii() {
        RedisProtocolReader reader = createRedisProtocolReader("foo\r\nbar\r\na");
        assertThat(reader.readLine(), is("foo"));
        assertThat(reader.readLine(), is("bar"));
        assertThat(reader.readLine(), is("a"));
        assertThat(reader.readLine(), nullValue());
    }

    @Test
    public void testGet3LineOfMultiBytes() {
        RedisProtocolReader reader = createRedisProtocolReader("フー\r\nバー\r\nあ");
        assertThat(reader.readLine(), is("フー"));
        assertThat(reader.readLine(), is("バー"));
        assertThat(reader.readLine(), is("あ"));
        assertThat(reader.readLine(), nullValue());
    }

    protected RedisProtocolReader createRedisProtocolReader(String str) {
        return new RedisProtocolReader(createChannelBuffer(str));
    }

    protected ChannelBuffer createChannelBuffer(String str) {
        ChannelBuffer cb = mock(ChannelBuffer.class);
        try {
            when(cb.array()).thenReturn(new String(str).getBytes("UTF-8"));
            return cb;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

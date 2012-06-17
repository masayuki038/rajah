package net.wrap_trap.rajah.protocol;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import net.wrap_trap.rajah.Request;

import org.apache.commons.lang3.StringUtils;
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
        assertThat(reader.readLine(), is("bar"));
        assertThat(reader.readLine(), nullValue());
    }

    @Test
    public void testGetLineOfAsciiTerminatedLF() {
        RedisProtocolReader reader = createRedisProtocolReader("foo\r\nbar\n");
        assertThat(reader.readLine(), is("foo"));
        assertThat(reader.readLine(), is("bar"));
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

    @Test
    public void testClientSet() throws RedisProtocolReadException {
        RedisProtocolReader reader = createRedisProtocolReader("*3", "$3", "SET", "$5", "mykey", "$7", "myvalue");
        Request request = reader.buildClientRequest();
        Object[] args = request.getArgs();
        assertThat(args.length, is(3));
        assertThat((String) args[0], is("SET"));
        assertThat((String) args[1], is("mykey"));
        assertThat((String) args[2], is("myvalue"));
    }

    @Test
    public void testClientSetWithMultiByteValue() throws RedisProtocolReadException {
        RedisProtocolReader reader = createRedisProtocolReader("*3", "$3", "SET", "$5", "mykey", "$18", "マイバリュー");
        Request request = reader.buildClientRequest();
        Object[] args = request.getArgs();
        assertThat(args.length, is(3));
        assertThat((String) args[0], is("SET"));
        assertThat((String) args[1], is("mykey"));
        assertThat((String) args[2], is("マイバリュー"));
    }

    @Test
    public void testClientNoLine() {
        RedisProtocolReader reader = createRedisProtocolReader("");
        try {
            reader.buildClientRequest();
            fail();
        } catch (RedisProtocolReadException e) {
            assertThat(e.getMessage().indexOf("not null"), not(-1));
        }
    }

    @Test
    public void testFirstLineDontStartWithAsterisk() {
        RedisProtocolReader reader = createRedisProtocolReader("1", "$3", "foo");
        try {
            reader.buildClientRequest();
            fail();
        } catch (RedisProtocolReadException e) {
            assertThat(e.getMessage().indexOf("start with '*'"), not(-1));
        }
    }

    @Test
    public void testFirstLineDontIndicateTheNumberOfArguments() {
        RedisProtocolReader reader = createRedisProtocolReader("*a", "$3", "foo");
        try {
            reader.buildClientRequest();
            fail();
        } catch (RedisProtocolReadException e) {
            assertThat(e.getMessage().indexOf("indicate the number of arguments"), not(-1));
        }
    }

    @Test
    public void testBytesArgumentLack() {
        RedisProtocolReader reader = createRedisProtocolReader("*3");
        try {
            reader.buildClientRequest();
            fail();
        } catch (RedisProtocolReadException e) {
            assertThat(e.getMessage().indexOf("The bytes of argument"), not(-1));
        }
    }

    @Test
    public void testValueArgumentLack() {
        RedisProtocolReader reader = createRedisProtocolReader("*3", "$3");
        try {
            reader.buildClientRequest();
            fail();
        } catch (RedisProtocolReadException e) {
            assertThat(e.getMessage().indexOf("The value of argument"), not(-1));
        }
    }

    @Test
    public void testByteArgumentDontStartWithDoller() {
        RedisProtocolReader reader = createRedisProtocolReader("*3", "3", "foo");
        try {
            reader.buildClientRequest();
            fail();
        } catch (RedisProtocolReadException e) {
            assertThat(e.getMessage().indexOf("start with '$'"), not(-1));
        }
    }

    protected RedisProtocolReader createRedisProtocolReader(String... args) {
        return new RedisProtocolReader(StringUtils.join(args, "\r\n"));
    }
}

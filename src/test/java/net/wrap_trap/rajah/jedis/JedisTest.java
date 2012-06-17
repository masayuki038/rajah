package net.wrap_trap.rajah.jedis;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import redis.clients.jedis.Jedis;

public class JedisTest {

    @Test
    public void testSetGet() {
        Jedis jedis = new Jedis("localhost", 8080, 10000);
        jedis.set("foo", "bar");
        String value = jedis.get("foo");
        assertThat(value, is("bar"));
        jedis.disconnect();
    }
}

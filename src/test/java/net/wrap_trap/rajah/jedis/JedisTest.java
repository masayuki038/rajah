package net.wrap_trap.rajah.jedis;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import redis.clients.jedis.Jedis;

public class JedisTest {

    //@Test
    public void testSetGet() {
        Jedis jedis = new Jedis("localhost", 8080, 10000);
        jedis.set("foo1", "bar1");
        jedis.set("foo2", "bar2");
        jedis.set("foo3", "bar3");

        assertThat(jedis.get("foo1"), is("bar1"));
        assertThat(jedis.get("foo2"), is("bar2"));
        assertThat(jedis.get("foo3"), is("bar3"));
        assertThat(jedis.exists("foo1"), is(true));
        assertThat(jedis.exists("foo2"), is(true));
        assertThat(jedis.exists("foo3"), is(true));

        long deleted = jedis.del("foo1", "foo2", "foo4");
        assertThat(deleted, is(2L));

        assertThat(jedis.exists("foo1"), is(false));
        assertThat(jedis.exists("foo2"), is(false));
        assertThat(jedis.exists("foo3"), is(true));

        jedis.disconnect();
    }
}

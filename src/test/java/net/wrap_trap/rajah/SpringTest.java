package net.wrap_trap.rajah;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringTest {

    @Test
    public void testWiring() {
        ApplicationContext appContext = new ClassPathXmlApplicationContext(
                                                                           "classpath:./META-INF/spring/test-context.xml");
        Bar bar = (Bar) appContext.getBean("bar");
        assertThat(bar.getFooString(), is("foobar"));
    }
}

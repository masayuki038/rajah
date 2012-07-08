package net.wrap_trap.rajah;

import javax.annotation.Resource;

public class Bar {

    @Resource(name = "foo")
    private Foo foo;

    public String getFooString() {
        return foo.foo();
    }
}

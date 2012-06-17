package net.wrap_trap.rajah.protocol;

public class Request {

    private Object[] args;

    public Request(Object[] args) {
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }
}

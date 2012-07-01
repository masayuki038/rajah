package net.wrap_trap.rajah;

public class Element {

    private Object value;

    private Long expire;

    public Element(Object value) {
        this(value, null);
    }

    public Element(Object value, Long expire) {
        super();
        this.value = value;
        this.expire = expire;
    }

    public Object getValue() {
        return value;
    }

    public Long getExpire() {
        return expire;
    }
}

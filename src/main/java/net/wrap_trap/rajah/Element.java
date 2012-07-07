package net.wrap_trap.rajah;

public class Element {

    private String key;

    private Object value;

    private Long expire;

    public Element(String key, Object value) {
        this(key, value, null);
    }

    public Element(String key, Object value, Long expire) {
        super();
        this.key = key;
        this.value = value;
        this.expire = expire;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    public Long getExpire() {
        return expire;
    }
}

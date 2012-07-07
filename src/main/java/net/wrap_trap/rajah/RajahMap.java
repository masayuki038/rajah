package net.wrap_trap.rajah;

import static net.wrap_trap.rajah.command.ExpirationHelper.isExpired;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class RajahMap implements Map<String, Element> {

    private Map<String, Element> core;
    private Set<Element> expires;

    public RajahMap(Map<String, Element> core, Set<Element> expires) {
        super();
        this.core = core;
        this.expires = expires;
    }

    public void clear() {
        core.clear();
    }

    public boolean containsKey(Object key) {
        return core.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return core.containsValue(value);
    }

    public Set<java.util.Map.Entry<String, Element>> entrySet() {
        return core.entrySet();
    }

    public boolean equals(Object o) {
        return core.equals(o);
    }

    public Element get(Object key) {
        Element e = core.get(key);
        if (e != null && isExpired(e)) {
            remove(key);
            this.expires.remove(e);
            return null;
        }
        return e;
    }

    public int hashCode() {
        return core.hashCode();
    }

    public boolean isEmpty() {
        return core.isEmpty();
    }

    public Set<String> keySet() {
        return core.keySet();
    }

    public Element put(String key, Element value) {
        return core.put(key, value);
    }

    public void putAll(Map<? extends String, ? extends Element> m) {
        core.putAll(m);
    }

    public Element remove(Object key) {
        return core.remove(key);
    }

    public int size() {
        return core.size();
    }

    public Collection<Element> values() {
        return core.values();
    }
}

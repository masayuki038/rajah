package net.wrap_trap.rajah;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class Database {

    private Map<String, Element> map;
    private Set<Element> expires;

    public Database() {
        this.expires = new TreeSet<Element>(new Comparator<Element>() {
            public int compare(Element t1, Element t2) {
                return t1.getExpire().compareTo(t2.getExpire());
            }
        });
        this.map = new RajahMap(new ConcurrentHashMap<String, Element>(), this.expires);
    }

    public Map<String, Element> getMap() {
        return map;
    }

    public Set<Element> getExpires() {
        return expires;
    }
}

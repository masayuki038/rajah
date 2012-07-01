package net.wrap_trap.rajah;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Database {

    private Map<String, Element> map;
    private Set<Element> expires;

    public Database() {
        this.map = new HashMap<String, Element>();
        this.expires = new TreeSet<Element>(new Comparator<Element>() {
            public int compare(Element t1, Element t2) {
                return t1.getExpire().compareTo(t2.getExpire());
            }
        });
    }

    public Map<String, Element> getMap() {
        return map;
    }

    public Set<Element> getExpires() {
        return expires;
    }
}

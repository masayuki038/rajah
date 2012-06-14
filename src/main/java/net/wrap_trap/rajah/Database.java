package net.wrap_trap.rajah;

import java.util.Map;

public class Database {

    private Map<String, Object> map;

    public Database(Map<String, Object> map) {
        this.map = map;
    }

    public Map<String, Object> getMap() {
        return map;
    }

}

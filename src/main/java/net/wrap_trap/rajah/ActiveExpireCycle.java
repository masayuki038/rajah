package net.wrap_trap.rajah;

import static net.wrap_trap.rajah.command.ExpirationHelper.isExpired;

import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

public class ActiveExpireCycle extends TimerTask {

    private Database database;

    private static final int REDIS_EXPIRELOOKUPS_PER_CRON = 10;

    public ActiveExpireCycle(Database database) {
        super();
        this.database = database;
    }

    @Override
    public void run() {
        Set<Element> expires = database.getExpires();
        Map<String, Element> map = database.getMap();
        int num = expires.size();
        if (num > REDIS_EXPIRELOOKUPS_PER_CRON) {
            num = REDIS_EXPIRELOOKUPS_PER_CRON;
        }

        for (Element e : expires) {
            if (isExpired(e)) {
                map.remove(e.getKey());
                database.getExpires().remove(e);
            }
            if (num-- > 0) {
                break;
            }
        }
    }
}

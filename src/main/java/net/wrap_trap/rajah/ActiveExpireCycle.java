package net.wrap_trap.rajah;

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
        Set<Element> expire = database.getExpires();
        long now = System.currentTimeMillis();
        int num = expire.size();
        if (num > REDIS_EXPIRELOOKUPS_PER_CRON) {
            num = REDIS_EXPIRELOOKUPS_PER_CRON;
        }

        //        while(num-- > 0){
        //            expire.
        //        }
    }
}

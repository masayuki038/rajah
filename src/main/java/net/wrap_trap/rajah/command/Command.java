package net.wrap_trap.rajah.command;

import java.util.ArrayList;
import java.util.List;

import net.wrap_trap.rajah.Client;

import com.google.common.base.Preconditions;

public enum Command {
    GET("get", 2, "w", 1, 1, 1, 1) {
        @Override
        public Object execute(Client client) {
            Object[] args = client.getArgs();
            Preconditions.checkArgument(args.length > 0);
            // TODO check the expiring for a key and propagate these to slaves;
            return client.getDatabase().getMap().get(args[1]);
        }
    },
    SET("set", 3, "wm", 0, 1, 1, 1) {
        @Override
        public Object execute(Client client) {
            Object[] args = client.getArgs();
            Preconditions.checkArgument(args.length > 1);
            client.getDatabase().getMap().put(args[1].toString(), args[2]);
            // TODO check the expiring for a key
            // TODO set REDIS_DIRTY_CAS to each client that watched the key.
            return null;
        }

        @Override
        public Integer[] getKeys(Object[] args) {
            // TODO preload key case.
            return getKeysUsingCommandTable(args);
        }
    };

    private final String name;
    private final int arity;
    private final String sFlags;
    private final int flags;
    private final int firstKey;
    private final int lastKey;
    private final int keyStep;

    private final long milliseconds;
    private final long calls;

    private Command(String name, int arity, String sFlags, int flags, int firstKey, int lastKey, int keyStep) {
        this.name = name;
        this.arity = arity;
        this.sFlags = sFlags;
        this.flags = flags;
        this.firstKey = firstKey;
        this.lastKey = lastKey;
        this.keyStep = keyStep;
        this.milliseconds = 0L;
        this.calls = 0L;
    }

    public abstract Object execute(Client client);

    public Integer[] getKeys(Object[] args) {
        return null;
    }

    protected Integer[] getKeysUsingCommandTable(Object[] args) {
        if (this.firstKey == 0) {
            return null;
        }

        int last = this.lastKey;
        if (last < 0) {
            last = args.length + last;
        }
        List<Integer> list = new ArrayList<Integer>();
        for (int j = this.firstKey; j <= last; j += this.keyStep) {
            list.add(j);
        }
        Integer[] ret = new Integer[list.size()];
        list.toArray(ret);
        return ret;
    }
}

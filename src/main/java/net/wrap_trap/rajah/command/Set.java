package net.wrap_trap.rajah.command;

import net.wrap_trap.rajah.Database;
import net.wrap_trap.rajah.OkReply;
import net.wrap_trap.rajah.Reply;
import net.wrap_trap.rajah.Request;

import com.google.common.base.Preconditions;

public class Set implements Command {

    public Reply execute(Request request, Database database) {
        Object[] args = request.getArgs();
        Preconditions.checkArgument(args.length > 1);
        database.getMap().put(args[1].toString(), args[2]);
        // TODO check the expiring for a key
        // TODO set REDIS_DIRTY_CAS to each client that watched the key.
        return new OkReply();
    }

}

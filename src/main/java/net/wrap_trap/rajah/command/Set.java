package net.wrap_trap.rajah.command;

import net.wrap_trap.rajah.Database;
import net.wrap_trap.rajah.Element;
import net.wrap_trap.rajah.protocol.OkReply;
import net.wrap_trap.rajah.protocol.Reply;
import net.wrap_trap.rajah.protocol.Request;

import com.google.common.base.Preconditions;

public class Set implements Command {

    public Reply execute(Request request, Database database) {
        String[] args = request.getArgs();
        Preconditions.checkArgument(args.length > 1);
        database.getMap().put(args[1], new Element(args[1], args[2]));
        // TODO check the expiring for a key
        // TODO set REDIS_DIRTY_CAS to each client that watched the key.
        return new OkReply();
    }
}

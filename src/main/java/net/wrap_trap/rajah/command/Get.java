package net.wrap_trap.rajah.command;

import net.wrap_trap.rajah.BulkReplies;
import net.wrap_trap.rajah.Database;
import net.wrap_trap.rajah.Reply;
import net.wrap_trap.rajah.Request;

import com.google.common.base.Preconditions;

public class Get implements Command {

    public Reply execute(Request request, Database database) {
        Object[] args = request.getArgs();
        Preconditions.checkArgument(args.length > 0);
        // TODO check the expiring for a key and propagate these to slaves;
        return new BulkReplies((String) database.getMap().get(args[1]));
    }
}

package net.wrap_trap.rajah.command;

import net.wrap_trap.rajah.Database;
import net.wrap_trap.rajah.IntegerReply;
import net.wrap_trap.rajah.Reply;
import net.wrap_trap.rajah.Request;

import com.google.common.base.Preconditions;

public class Exists implements Command {

    public Reply execute(Request request, Database database) {
        Object[] args = request.getArgs();
        Preconditions.checkArgument(args.length == 2);

        boolean containsKey = database.getMap().containsKey(args[1]);
        return new IntegerReply(containsKey ? 1 : 0);
    }
}

package net.wrap_trap.rajah.command;

import net.wrap_trap.rajah.Database;
import net.wrap_trap.rajah.protocol.IntegerReply;
import net.wrap_trap.rajah.protocol.Reply;
import net.wrap_trap.rajah.protocol.Request;

import com.google.common.base.Preconditions;

public class Del implements Command {

    public Reply execute(Request request, Database database) {
        Object[] args = request.getArgs();
        Preconditions.checkArgument(args.length > 1);
        int removed = 0;

        for (int i = 1; i < args.length; i++) {
            Object o = database.getMap().remove(args[i].toString());
            if (o != null) {
                removed++;
            }
        }
        return new IntegerReply(removed);
    }
}

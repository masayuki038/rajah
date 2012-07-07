package net.wrap_trap.rajah.command;

import net.wrap_trap.rajah.Database;
import net.wrap_trap.rajah.Element;
import net.wrap_trap.rajah.protocol.IntegerReply;
import net.wrap_trap.rajah.protocol.Reply;
import net.wrap_trap.rajah.protocol.Request;

import com.google.common.base.Preconditions;

public class Exists implements Command {

    public Reply execute(Request request, Database database) {
        String[] args = request.getArgs();
        Preconditions.checkArgument(args.length == 2);

        Element e = database.getMap().get(args[1]);
        return new IntegerReply((e != null) ? 1 : 0);
    }
}

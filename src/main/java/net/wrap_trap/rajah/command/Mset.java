package net.wrap_trap.rajah.command;

import java.util.Map;

import net.wrap_trap.rajah.Database;
import net.wrap_trap.rajah.protocol.OkReply;
import net.wrap_trap.rajah.protocol.Reply;
import net.wrap_trap.rajah.protocol.Request;

import com.google.common.base.Preconditions;

public class Mset implements Command {

    public Reply execute(Request request, Database database) {
        Object[] args = request.getArgs();
        Preconditions.checkArgument(args.length > 1, String.format("args.length: %d <= 1", args.length));
        Preconditions.checkArgument(((args.length % 2) == 1), String.format("args.length: %s mod 2 != 1", args.length));

        Map<String, Object> map = database.getMap();

        for (int i = 1; i < args.length; i = i + 2) {
            map.put(String.valueOf(args[i]), args[i + 1]);
        }

        return new OkReply();
    }
}

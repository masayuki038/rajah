package net.wrap_trap.rajah.command;

import java.util.Map;

import net.wrap_trap.rajah.Database;
import net.wrap_trap.rajah.protocol.MultiBulkReplies;
import net.wrap_trap.rajah.protocol.Reply;
import net.wrap_trap.rajah.protocol.Request;

import com.google.common.base.Preconditions;

public class Mget implements Command {

    public Reply execute(Request request, Database database) {
        Object[] args = request.getArgs();
        Preconditions.checkArgument(args.length > 1);

        MultiBulkReplies replies = new MultiBulkReplies();
        Map<String, Object> map = database.getMap();

        for (int i = 1; i < args.length; i++) {
            Object value = map.get(args[i]);
            if (value == null) {
                replies.add(null);
            } else {
                replies.add(String.valueOf(value));
            }
        }

        return replies;
    }
}

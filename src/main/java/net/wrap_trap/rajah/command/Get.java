package net.wrap_trap.rajah.command;

import java.util.Map;

import net.wrap_trap.rajah.Database;
import net.wrap_trap.rajah.Element;
import net.wrap_trap.rajah.protocol.BulkReplies;
import net.wrap_trap.rajah.protocol.Reply;
import net.wrap_trap.rajah.protocol.Request;

import com.google.common.base.Preconditions;

public class Get implements Command {

    public Reply execute(Request request, Database database) {
        String[] args = request.getArgs();
        Preconditions.checkArgument(args.length == 2);
        // TODO check the expiring for a key and propagate these to slaves;

        Map<String, Element> map = database.getMap();
        Element e = map.get(args[1]);
        if (e == null) {
            return new BulkReplies(null);
        }
        return new BulkReplies(String.valueOf(e.getValue()));
    }
}

package net.wrap_trap.rajah.command;

import static net.wrap_trap.rajah.command.ExpirationHelper.expireDateFromNow;
import net.wrap_trap.rajah.Database;
import net.wrap_trap.rajah.Element;
import net.wrap_trap.rajah.protocol.OkReply;
import net.wrap_trap.rajah.protocol.Reply;
import net.wrap_trap.rajah.protocol.Request;

import com.google.common.base.Preconditions;

public class SetEx implements Command {

    public Reply execute(Request request, Database database) {
        String[] args = request.getArgs();
        Preconditions.checkArgument(args.length == 4);
        int expire = Integer.parseInt(args[2]);
        Element e = new Element(args[3], expireDateFromNow(expire * 1000L));
        database.getMap().put(args[1], e);
        database.getExpires().add(e);
        // TODO check the expiring for a key
        // TODO set REDIS_DIRTY_CAS to each client that watched the key.
        return new OkReply();
    }
}

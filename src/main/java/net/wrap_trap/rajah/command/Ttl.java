package net.wrap_trap.rajah.command;

import com.google.common.base.Preconditions;

import net.wrap_trap.rajah.Database;
import net.wrap_trap.rajah.Element;
import net.wrap_trap.rajah.protocol.IntegerReply;
import net.wrap_trap.rajah.protocol.Reply;
import net.wrap_trap.rajah.protocol.Request;

public class Ttl implements Command {

	public Reply execute(Request request, Database database) {
        String[] args = request.getArgs();
        Preconditions.checkArgument(args.length == 2);
        Element e = database.getMap().get(args[1]);
        if (e == null) {
        	return new IntegerReply(-1);
        }
        Long expire = e.getExpire();
        if(expire == null) {
        	return new IntegerReply(-1);
        }
        long remaingMs = ExpirationHelper.remainingFromNow(expire);
        return new IntegerReply((int)(remaingMs / 1000L));
	}
}

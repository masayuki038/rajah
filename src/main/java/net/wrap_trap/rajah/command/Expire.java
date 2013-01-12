package net.wrap_trap.rajah.command;

import static net.wrap_trap.rajah.command.ExpirationHelper.expireDateFromNow;

import java.util.Map;

import com.google.common.base.Preconditions;

import net.wrap_trap.rajah.Database;
import net.wrap_trap.rajah.Element;
import net.wrap_trap.rajah.protocol.IntegerReply;
import net.wrap_trap.rajah.protocol.Reply;
import net.wrap_trap.rajah.protocol.Request;

public class Expire implements Command {

	public Reply execute(Request request, Database database) {
        String[] args = request.getArgs();
        Preconditions.checkArgument(args.length == 3);
        Map<String, Element> map = database.getMap();
        Element e = database.getMap().get(args[1]);
        if (e == null) {
        	return new IntegerReply(0);
        }
        int expire = Integer.parseInt(args[2]);
        map.put(e.getKey(), new Element(e.getKey(), e.getValue(), expireDateFromNow(expire * 1000L)));
        return new IntegerReply(1);
	}

}

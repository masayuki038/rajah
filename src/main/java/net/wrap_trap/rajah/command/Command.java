package net.wrap_trap.rajah.command;

import net.wrap_trap.rajah.Database;
import net.wrap_trap.rajah.Reply;
import net.wrap_trap.rajah.Request;

public interface Command {

    Reply execute(Request request, Database database);

}

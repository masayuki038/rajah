package net.wrap_trap.rajah.channel;

import java.util.HashMap;
import java.util.Map;

import net.wrap_trap.rajah.Database;
import net.wrap_trap.rajah.command.Command;
import net.wrap_trap.rajah.command.Del;
import net.wrap_trap.rajah.command.Exists;
import net.wrap_trap.rajah.command.Get;
import net.wrap_trap.rajah.command.Mget;
import net.wrap_trap.rajah.command.Mset;
import net.wrap_trap.rajah.command.Set;
import net.wrap_trap.rajah.protocol.RedisProtocolReadException;
import net.wrap_trap.rajah.protocol.RedisProtocolReader;
import net.wrap_trap.rajah.protocol.RedisProtocolWriteException;
import net.wrap_trap.rajah.protocol.Reply;
import net.wrap_trap.rajah.protocol.Request;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandChannelHandler extends SimpleChannelHandler {

    protected static Logger logger = LoggerFactory.getLogger(CommandChannelHandler.class);
    private Database database;

    private Map<String, Command> commandMap;

    public CommandChannelHandler() {
        commandMap = new HashMap<String, Command>();
        commandMap.put("GET", new Get());
        commandMap.put("SET", new Set());
        commandMap.put("DEL", new Del());
        commandMap.put("EXISTS", new Exists());
        commandMap.put("MGET", new Mget());
        commandMap.put("MSET", new Mset());

        database = new Database(new HashMap<String, Object>());
    }

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (logger.isInfoEnabled() && e instanceof ChannelStateEvent) {
            logger.info(e.toString());
        }
        super.handleUpstream(ctx, e);
    }

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (logger.isInfoEnabled() && e instanceof ChannelStateEvent) {
            logger.info(e.toString());
        }
        super.handleDownstream(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        String in = (String) e.getMessage();
        if (logger.isInfoEnabled()) {
            logger.info(in);
        }

        RedisProtocolReader reader = new RedisProtocolReader(in);
        try {
            Request request = reader.buildClientRequest();
            String commandName = (String) request.getArgs()[0];

            Command command = commandMap.get(commandName);
            if (command == null) {
                throw new IllegalArgumentException(String.format("command: %s is not registered.", commandName));
            }
            Reply reply = command.execute(request, database);
            reply.write(e.getChannel());
        } catch (RedisProtocolReadException re) {
            throw new RuntimeException(re);
        } catch (RedisProtocolWriteException we) {
            throw new RuntimeException(we);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        logger.error("error has occured.", e.getCause());
        Channel ch = e.getChannel();
        ch.close();
        ctx.sendUpstream(e);
    }
}

package net.wrap_trap.rajah.channel;

import java.util.HashMap;

import net.wrap_trap.rajah.Database;
import net.wrap_trap.rajah.Reply;
import net.wrap_trap.rajah.Request;
import net.wrap_trap.rajah.command.Command;
import net.wrap_trap.rajah.protocol.RedisProtocolReadException;
import net.wrap_trap.rajah.protocol.RedisProtocolReader;
import net.wrap_trap.rajah.protocol.RedisProtocolWriteException;

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
    private Database database = new Database(new HashMap<String, Object>());

    public CommandChannelHandler() {}

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
            String command = (String) request.getArgs()[0];
            Reply reply;
            if (command.equals("SET")) {
                reply = Command.SET.execute(request, database);
            } else if (command.equals("GET")) {
                reply = Command.GET.execute(request, database);
            } else {
                throw new IllegalArgumentException(String.format("command: %s is unacceptable.", command));
            }
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
    }
}

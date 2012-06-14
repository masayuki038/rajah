package net.wrap_trap.rajah.channel;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandChannelHandler extends SimpleChannelHandler {

    protected static Logger logger = LoggerFactory.getLogger(CommandChannelHandler.class);

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        ChannelBuffer m = (ChannelBuffer) e.getMessage();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
        logger.error("error has occured.", e);

        Channel ch = e.getChannel();
        ch.close();
    }
}

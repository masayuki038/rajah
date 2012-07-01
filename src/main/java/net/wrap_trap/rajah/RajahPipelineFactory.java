package net.wrap_trap.rajah;

import net.wrap_trap.rajah.channel.CommandChannelHandler;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

public class RajahPipelineFactory implements ChannelPipelineFactory {

    private Database database;

    public RajahPipelineFactory(Database database) {
        this.database = database;
    }

    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("ecoder", new StringEncoder());
        pipeline.addLast("handler", new CommandChannelHandler(database));
        return pipeline;
    }

}

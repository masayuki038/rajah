package net.wrap_trap.rajah;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap {

    private final int port;

    protected static Logger logger = LoggerFactory.getLogger(Bootstrap.class);

    public Bootstrap(int port) {
        this.port = port;
    }

    public void run() {
        ChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
                                                                   Executors.newCachedThreadPool());
        ServerBootstrap bootstrap = new ServerBootstrap(factory);
        bootstrap.setPipelineFactory(new RajahPipelineFactory());

        bootstrap.bind(new InetSocketAddress(port));
        if (logger.isInfoEnabled()) {
            logger.info("server start");
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new Bootstrap(port).run();
    }
}

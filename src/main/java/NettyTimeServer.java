import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.Date;

/**
 * create with PACKAGE_NAME
 * USER: husterfox
 */
public class NettyTimeServer {
    private int port;

    public NettyTimeServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 18080;
        }
        new NettyTimeServer(port).run();
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new TimeEncoder(), new TimeServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    class TimeServerHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ChannelFuture f = ctx.writeAndFlush(new UnixTime());
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    class TimeEncoder extends MessageToByteEncoder<UnixTime> {
        @Override
        protected void encode(ChannelHandlerContext ctx, UnixTime msg, ByteBuf out) {
            out.writeInt((int)msg.value());
        }
    }

    static class UnixTime {

        private final long value;

        public UnixTime() {
            this(System.currentTimeMillis() / 1000L + 2208988800L);
        }

        public UnixTime(long value) {
            this.value = value;
        }

        public long value() {
            return value;
        }

        @Override
        public String toString() {
            return new Date((value() - 2208988800L) * 1000L).toString();
        }
    }
}

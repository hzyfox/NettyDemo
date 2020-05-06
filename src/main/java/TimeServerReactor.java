import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * create with PACKAGE_NAME
 * USER: husterfox
 */
public class TimeServerReactor implements Runnable {
    Selector selector;
    ServerSocketChannel serverSocketChannel;
    SelectionKey key;

    public TimeServerReactor(int port) {
        try {
            selector = Selector.open();
            InetSocketAddress socketAddress = new InetSocketAddress(port);
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(socketAddress);
            serverSocketChannel.configureBlocking(false);
            key = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            key.attach(new Acceptor());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Server listening on port: " + serverSocketChannel.socket().getLocalPort());

        while (!Thread.interrupted()) {
            try {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                for (SelectionKey key : keys) {
                    dispatch(key);
                }
                keys.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void dispatch(SelectionKey key) {
        Runnable runnable = (Runnable) key.attachment();
        if (runnable != null) {
            runnable.run();
        }
    }

    private class Acceptor implements Runnable {
        @Override
        public void run() {
            try {
                SocketChannel channel = serverSocketChannel.accept();
                if (channel != null) {
                    new TimeServerHandler(selector, channel);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        new TimeServerReactor(52233).run();
    }
}


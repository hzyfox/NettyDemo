package reacrtor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;



public class ServerReactor implements Runnable {

    private SelectorProvider selectorProvider = SelectorProvider.provider();
    private ServerSocketChannel serverSocketChannel;

    public ServerReactor(int port) throws IOException {
        serverSocketChannel = selectorProvider.openServerSocketChannel(); //ServerSocketChannel.open();
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(port), 1024);
        serverSocketChannel.configureBlocking(false);
//        logger.debug(String.format("Server : Server Start.----%d", port));
    }

    @Override
    public void run() {
        try {
            System.out.println("[Thread-" + Thread.currentThread().getId() + "] run dispatch");
            new ServerDispatcher(serverSocketChannel, selectorProvider).execute();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}


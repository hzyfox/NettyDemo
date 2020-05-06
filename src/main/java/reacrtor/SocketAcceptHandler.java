package reacrtor;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


public class SocketAcceptHandler extends SocketHandler {

    public SocketAcceptHandler(ServerDispatcher dispatcher, ServerSocketChannel sc, Selector selector)
            throws IOException {
        super(dispatcher, sc, selector);
        serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT, this);
    }

    @Override
    public void runnerExecute(int readyKeyOps, SocketChannel socketChannel) throws IOException {
        System.out.println("[Thread-" + Thread.currentThread().getId() + "] run ito RunnerExecutor readyOps: " + readyKeyOps);
        // TODO Auto-generated method stub
        if (readyKeyOps == SelectionKey.OP_ACCEPT) {
            socketChannel = serverSocketChannel.accept();
            System.out.println("[Thread-" + Thread.currentThread().getId() + "] accept SocketChannel: " + socketChannel);
            socketChannel.configureBlocking(false);
            dispatcher.getWriteSelector().wakeup();
            dispatcher.getReadSelector().wakeup();
            dispatcher.getAcceptSelector().wakeup();
            socketChannel.register(dispatcher.getReadSelector(), SelectionKey.OP_READ);
            System.out.println("[Thread-" + Thread.currentThread().getId() + "] register ReadSelector: " + socketChannel);
            ChannelState.state.put(socketChannel, State.Reading);
        }
    }
}


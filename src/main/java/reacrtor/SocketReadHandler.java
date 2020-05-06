package reacrtor;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SocketReadHandler extends SocketHandler {
    private SelectionKey selectionKey;
    private int BLOCK = 4096;
    private ByteBuffer receivebuffer = ByteBuffer.allocate(BLOCK);
    public static ExecutorService pool = Executors.newFixedThreadPool(2);

    public SocketReadHandler(ServerDispatcher dispatcher, ServerSocketChannel sc, Selector selector) throws IOException {
        super(dispatcher, sc, selector);
    }

    @Override
    public void runnerExecute(int readyKeyOps, SocketChannel socketChannel) throws IOException {
        // TODO Auto-generated method stub
        int count = 0;
        System.out.println("[Thread-" + Thread.currentThread().getId() + "] Read runner: " + readyKeyOps);
        if (SelectionKey.OP_READ == readyKeyOps
                && ChannelState.state.get(socketChannel) == State.Reading) {
            receivebuffer.clear();
            count = socketChannel.read(receivebuffer);
            if (count > 0) {
                ChannelState.state.put(socketChannel, State.Processing);
                receivebuffer.flip();
                byte[] bytes = new byte[receivebuffer.remaining()];
                receivebuffer.get(bytes);
                String body = new String(bytes, "UTF-8");
                System.out.println("[Thread-" + Thread.currentThread().getId() + "] received data: " + body);
                pool.execute(() -> this.process(socketChannel));
            }
        }
    }

    public synchronized void process(SocketChannel socketChannel) {
        if (ChannelState.state.get(socketChannel) == State.Processing) {
            System.out.println("[Thread-" + Thread.currentThread().getId() + "] is processing");
            try {
                dispatcher.getWriteSelector().wakeup();
                dispatcher.getReadSelector().wakeup();
                dispatcher.getAcceptSelector().wakeup();
                socketChannel.register(dispatcher.getWriteSelector(), SelectionKey.OP_WRITE);
                System.out.println("[Thread-" + Thread.currentThread().getId() + "] register write selector: " + socketChannel);
                ChannelState.state.put(socketChannel, State.Writing);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        }
    }
}



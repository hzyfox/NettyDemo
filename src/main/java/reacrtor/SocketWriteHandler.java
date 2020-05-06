package reacrtor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;


public class SocketWriteHandler extends SocketHandler {
    private int BLOCK = 4096;
    private ByteBuffer sendbuffer;
    private static int Index = 1;

    public SocketWriteHandler(ServerDispatcher dispatcher, ServerSocketChannel sc, Selector selector) throws IOException {
        super(dispatcher, sc, selector);
    }

    @Override
    public void runnerExecute(int readyKeyOps, SocketChannel socketChannel) throws IOException {
        // TODO Auto-generated method stub

        if (readyKeyOps == SelectionKey.OP_WRITE
                && ChannelState.state.get(socketChannel) == State.Writing) {
            String data = String.format("%d", Index);
            byte[] req = data.getBytes();
            sendbuffer = ByteBuffer.wrap(data.getBytes());
//            sendbuffer.put(req);
//            sendbuffer.flip();
            socketChannel.write(sendbuffer);
            socketChannel.close();
            System.out.println("[Thread-" + Thread.currentThread().getId() + "] write data: " + Index);
            Index++;
            dispatcher.getReadSelector().wakeup();
//            socketChannel.register(dispatcher.getReadSelector(), SelectionKey.OP_READ);
//            ChannelState.state.put(socketChannel, State.Reading);
        }
    }
}


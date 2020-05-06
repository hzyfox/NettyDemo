package reacrtor;


import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public abstract class SocketHandler implements Runnable {
    protected Selector selector;
    protected SocketChannel socketChannel = null;
    protected ServerSocketChannel serverSocketChannel;
    protected ServerDispatcher dispatcher;
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public SocketHandler(ServerDispatcher dispatcher, ServerSocketChannel sc, Selector selector) throws IOException {
        this.selector = selector;
        this.serverSocketChannel = sc;
        this.dispatcher = dispatcher;
    }

    public abstract void runnerExecute(int readyKeyOps, SocketChannel socketChannel) throws IOException;

    @Override
    public final void run() {
        while (true) {
            readWriteLock.writeLock().lock();
            try {
                int keyOps = this.selector.select();
                boolean flag = keyOps > 0;
                if (flag) {
                    Set readyKeySet = selector.selectedKeys();
                    Iterator iterator = readyKeySet.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = (SelectionKey) iterator.next();
                        keyOps = key.readyOps();
                        if (keyOps == SelectionKey.OP_READ || keyOps == SelectionKey.OP_WRITE) {
                            socketChannel = (SocketChannel) key.channel();
                            socketChannel.configureBlocking(false);
                        }
                        runnerExecute(keyOps, socketChannel);
                    }
                    readyKeySet.clear();
                }
            } catch (Exception e) {
                // TODO: handle exception
            } finally {
                readWriteLock.writeLock().unlock();
            }
        }
    }


}


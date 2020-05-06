

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * create with PACKAGE_NAME
 * USER: husterfox
 */
public class TimeServerHandler implements Runnable {

    public final static int READ = 0, PROCESS = 1, WRITE = 2;
    public int state=READ;
    public SocketChannel socketChannel;
    public volatile boolean isClosed;
    public SelectionKey key;
    public static ExecutorService pool = Executors.newFixedThreadPool(2);
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    String data;

    public TimeServerHandler(Selector selector, SocketChannel socketChannel) throws IOException {

        this.socketChannel = socketChannel;
        socketChannel.configureBlocking(false);
        isClosed = !socketChannel.isConnected();
        key = this.socketChannel.register(selector, SelectionKey.OP_READ);
        key.attach(this);
        key.interestOps(SelectionKey.OP_READ);
        selector.wakeup();

    }

    @Override
    public void run() {
        System.out.println("[Thread-" + Thread.currentThread().getId() +"] Handler is running!!!!");
        try {
            if (isClosed) {
                socketChannel.close();
                return;
            }
            if (state == READ) {
                this.read();
            } else if (state == WRITE) {
                this.write();
            }

        } catch (IOException e) {

            try {
                socketChannel.close();
            } catch (IOException e1) {

            }
        }
    }

    public void read() throws IOException {
        System.out.println("[thread-" + Thread.currentThread().getId() + "] read data from client.");
        int readCount = socketChannel.read(buffer);
        if (readCount > 0) {
            state = PROCESS;
            pool.execute(() -> this.process(readCount));
        } else {
            this.isClosed = true;
        }
        key.interestOps(SelectionKey.OP_WRITE);
    }

    public void write() throws IOException {
        System.out.println("[thread-" + Thread.currentThread().getId() + "] write data to client : " + this.data);
        ByteBuffer output = ByteBuffer.wrap(("Hello " + this.data + "\n").getBytes());
        socketChannel.write(output);
        key.interestOps(SelectionKey.OP_READ);
        state = READ;
    }

    public void process(int readCount) {
        System.out.println("[thread-" + Thread.currentThread().getId() + "] is processing data.");
        buffer.flip();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        Date date = new Date();
        byte[] time = new byte[readCount];
        System.arraycopy(buffer.array(), 0, time, 0, readCount);
        date.setTime(Long.valueOf(new String(time).trim()));
        data = simpleDateFormat.format(date);
        System.out.println("[thread-" + Thread.currentThread().getId() + "] is after process:" + data);
        buffer.clear();
        state = WRITE;
    }
}


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Handler implements Runnable {

    private static final int READ = 0, PROCESS = 1, WRITE = 2;

    private final SocketChannel socketChannel;
    private final SelectionKey selectionKey;

    private static ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private ByteBuffer input = ByteBuffer.allocate(1024);
    private boolean isClosed;
    private int state = READ;
    private String data;

    public Handler(Selector selector, SocketChannel channel) throws IOException {
        this.socketChannel = channel;
        this.socketChannel.configureBlocking(false);
        this.isClosed = !socketChannel.isConnected();
        this.selectionKey = this.socketChannel.register(selector, SelectionKey.OP_READ);
        this.selectionKey.attach(this);
        this.selectionKey.interestOps(SelectionKey.OP_READ);
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
            if (READ == state) {
                this.read();
            } else if (WRITE == state) {
                this.write();
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socketChannel.close();
            } catch (IOException e1) {
                e1.printStackTrace();
                // ignore
            }
        }
    }

    private void read() throws IOException {
        System.out.println("[thread-" + Thread.currentThread().getId() + "] read data from client.");
        int readCount = socketChannel.read(input);
        if (readCount > 0) {
            state = PROCESS;
            pool.execute(() -> this.process(readCount));
        } else {
            this.isClosed = true;
        }
        selectionKey.interestOps(SelectionKey.OP_WRITE);
    }

    private void process(int readCount) {
        System.out.println("[thread-" + Thread.currentThread().getId() + "] is processing data.");
        StringBuilder sb = new StringBuilder();
        input.flip();
        byte[] subStringBytes = new byte[readCount];
        byte[] array = input.array();
        System.arraycopy(array, 0, subStringBytes, 0, readCount);
        sb.append(new String(subStringBytes));
        input.clear();
        this.data = sb.toString().trim();
        state = WRITE;
    }

    private void write() throws IOException {
        System.out.println("[thread-" + Thread.currentThread().getId() + "] write data to client : " + this.data);
        ByteBuffer output = ByteBuffer.wrap(("Hello " + this.data + "\n").getBytes());
        socketChannel.write(output);
        selectionKey.interestOps(SelectionKey.OP_READ);
        state = READ;
    }

}


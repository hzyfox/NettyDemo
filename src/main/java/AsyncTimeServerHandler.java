import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * create with PACKAGE_NAME
 * USER: husterfox
 * @author husterfox
 */
public class AsyncTimeServerHandler implements Runnable {
    CountDownLatch latch;
    AsynchronousServerSocketChannel asynchronousServerSocketChannel;
    private int port;

    public AsyncTimeServerHandler(int port) {
        this.port = port;
        try {
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("The AIO Time Server is start at port: " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        latch = new CountDownLatch(1);
        doAccept();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void doAccept() {
        asynchronousServerSocketChannel.accept(this, new CompletionHandler<AsynchronousSocketChannel, AsyncTimeServerHandler>() {
            @Override
            public void completed(AsynchronousSocketChannel result, AsyncTimeServerHandler attachment) {
                attachment.asynchronousServerSocketChannel.accept(attachment, this);
                final ByteBuffer buffer = ByteBuffer.allocate(1024);
                final AsynchronousSocketChannel sc = result;
                result.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        attachment.flip();
                        byte[] body = new byte[attachment.remaining()];
                        attachment.get(body);
                        try {
                            String req = new String(body, "UTF-8");
                            System.out.println("The AIO TimeServer receive order: " + req);
                            String currentTime = "TIME".equalsIgnoreCase(req) ? new java.util.Date(System.currentTimeMillis()).toString()
                                    : "Input Order: \"TIME\"";
                            doWrite(currentTime, sc);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        try {
                            sc.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    public void doWrite(String cuurentTime, final AsynchronousSocketChannel channel) {
                        if (cuurentTime != null && cuurentTime.trim().length() > 0) {
                            byte[] bytes = cuurentTime.getBytes();
                            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
                            writeBuffer.put(bytes);
                            writeBuffer.flip();
                            channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                                @Override
                                public void completed(Integer result, ByteBuffer attachment) {
                                    if (attachment.hasRemaining()) {
                                        channel.write(buffer, buffer, this);
                                    }
                                }

                                @Override
                                public void failed(Throwable exc, ByteBuffer attachment) {
                                    try {
                                        channel.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                    }
                });
            }

            @Override
            public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
                exc.printStackTrace();
                attachment.latch.countDown();
            }
        });
    }
}

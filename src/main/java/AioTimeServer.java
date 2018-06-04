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
 */
public class AioTimeServer {
    public static void main(String[] args) {
        int port = 18080;
        if (args != null && args.length > 0) {
            try {
                Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                //take default value
            }
        }
        new Thread(new AsyncTimeServerHandler(port), "AIO-Server").start();
    }


}

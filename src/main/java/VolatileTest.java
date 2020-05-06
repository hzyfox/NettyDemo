import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * create with PACKAGE_NAME
 * USER: husterfox
 */
public class VolatileTest {
    private static boolean stop;
    public static void main(String[] args) throws InterruptedException, IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("SubScribeReq.proto");
        System.out.println(url);
    }
}

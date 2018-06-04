import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * create with PACKAGE_NAME
 * USER: husterfox
 */
public class NioTimeClient {
    public static void main(String[] args) {
        int port = 18080;
        if(args.length > 0){
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                //e.printStackTrace();
            }
        }
        new Thread(new NioTimeClientHandle("127.0.0.1",port),"Time_Clinet_Thread0").start();
    }
}

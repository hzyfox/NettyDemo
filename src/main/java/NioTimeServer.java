import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * create with PACKAGE_NAME
 * USER: husterfox
 */
public class NioTimeServer {
    public static void main(String[] args) {
        int port = 18080;
        if(args.length > 0){
            try{
                port = Integer.valueOf(args[0]);
            }catch (NumberFormatException e){
                //
            }
        }
        new Thread(new MultiplexerTimeServer(port), "Ni0_TimeServer_Thread0").start();
    }
}

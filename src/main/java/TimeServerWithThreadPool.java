import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * create with PACKAGE_NAME
 * USER: husterfox
 */
public class TimeServerWithThreadPool {
    public static void main(String[] args) {
        int port = 18080;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                //take default port
            }
        }
        ServerSocket server = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            server = new ServerSocket(port);
            System.out.println("The timer server start at port: " + port);
            Socket socket = null;
            TimerServerHandlerExecutor executor = new TimerServerHandlerExecutor(50, 10000);
            while (true) {
                socket = server.accept();
                executor.execute(new TimerServerHandler(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                System.out.println("The time server close");
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                server = null;
            }
        }
    }
}

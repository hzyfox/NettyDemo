import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Client {

    private String host;
    private int port;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void sayHello() throws Exception {
        ExecutorService es = Executors.newCachedThreadPool();
        List<Callable<Boolean>> tasks = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            tasks.add(() -> {
                Socket socket = null;
                PrintWriter out = null;
                BufferedReader in = null;
                try {
                    socket = new Socket(host, port);
                    out = new PrintWriter(socket.getOutputStream(), true);

                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    System.out.println("Client[" + Thread.currentThread().getId() + "] connect success, host : " + host + " port: " + port);

                    String hay = String.valueOf(System.currentTimeMillis());
                    System.out.println("Client[" + Thread.currentThread().getId() + "] send data : " + hay);

                    out.println(hay);
                    String msg = in.readLine().trim();
                    System.out.println("Client[" + Thread.currentThread().getId() + "] receive data from server : " + msg);
//                    if(!("Hello " + hay).equals(msg)) {
//                        System.err.println("expect : " + hay + ", but : " + msg);
//                        System.exit(-1);
//                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (null != out) {
                        out.close();
                    }
                    if (null != in) {
                        in.close();
                    }
                    if (null != socket) {
                        socket.close();
                    }
                }
                return false;
            });
        }
        List<Future<Boolean>> futures = es.invokeAll(tasks);
        for (final Future<Boolean> future : futures) {
            future.get();
        }
        TimeUnit.SECONDS.sleep(5);
        es.shutdown();
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client("localhost", 8008);
        client.sayHello();
    }

}


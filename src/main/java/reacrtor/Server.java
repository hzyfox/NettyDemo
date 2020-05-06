package reacrtor;


import java.io.IOException;

public class Server {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        int port = 8008;
        System.out.println("StartSever at port: " + port);
        new ServerReactor(port).run();
    }
}




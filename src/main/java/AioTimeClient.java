/**
 * create with PACKAGE_NAME
 * USER: husterfox
 */
public class AioTimeClient {
    public static void main(String[] args) {
        int port = 18080;
        if (args != null && args.length > 0) {
            try {
                Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                //take default value
            }
        }
        new Thread(new AsyncTimeClientHandler("127.0.0.1",port)).start();
    }
}

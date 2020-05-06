/**
 * create with PACKAGE_NAME
 * USER: husterfox
 */
public class TestRunnable implements  Runnable {
    @Override
    public void run() {
        System.out.println("I am running");
    }

    public static void main(String[] args) {
        new TestRunnable();
    }
}


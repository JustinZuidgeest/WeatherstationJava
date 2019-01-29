package weatherServer;

import weatherIO.WeatherIO;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherServer {
    private static final int PORT = 54871;
    private static final int maxConnections = 800;
    private static boolean running = true;
    public static Semaphore sem = new Semaphore(maxConnections);
    public static WeatherIO wio = new WeatherIO();
    private static ExecutorService pool = Executors.newFixedThreadPool(800);

    public static void main(String[] args) {
        Socket con;
        try {
            ServerSocket server = new ServerSocket(PORT);
            while (running) {
                con = server.accept();
                pool.execute(new WorkerThread(con));
            }
        }
        catch (java.io.IOException ioe) {}
    }

    private boolean isRunning() {
        return running;
    }
}

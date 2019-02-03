package weatherServer;

import weatherIO.WeatherIO;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherServer {
    private static final int PORT = 54871;
    private static ExecutorService pool = Executors.newFixedThreadPool(800);
    static WeatherIO wio = new WeatherIO();

    public static void main(String[] args) {
        Socket con;
        try {
            ServerSocket server = new ServerSocket(PORT);
            while (true) {
                con = server.accept();
                pool.execute(new WeatherWorker(con));
            }
        }
        catch (java.io.IOException ioe) { System.out.println("WeatherServer ioe: " + ioe); }
    }
}
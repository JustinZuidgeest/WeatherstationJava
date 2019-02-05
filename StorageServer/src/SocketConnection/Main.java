package SocketConnection;

import java.io.FileWriter;
import java.net.*;

import ServerIO.IOWorker;
import ServerIO.FileWatcher;

/**
 * Opens and listens to new socket connections
 * Creates a thread for every new socket connection and hands it off to ConnectionWorker class
 */
public class Main {

    private static final int PORT = 54872;
    private static final int maxConnections = 32;
    static Semaphore sem = new Semaphore(maxConnections);

    public static final String datasharePath = "DataShare/";
    public static final String rawPath = "DataShare/raw/";
    public static final String minutePath = "DataShare/minute/";
    public static final String hourPath = "DataShare/hour/";
    public static final String dayPath = "DataShare/day/";

    public static IOWorker ioWorker = new IOWorker();
    private static FileWatcher fileWatcher = new FileWatcher();

    public static void main(String[] args) {
        //Save the local ip address to the remote server as fallback for DDNS
        try {
            String localIP = InetAddress.getLocalHost().getHostAddress();
            System.out.println("local host ip:" +  localIP);
            ioWorker.writeFile( datasharePath +  "ip.txt", localIP + "  -  ");
        }catch (UnknownHostException uhException){
            System.out.println("Unknown host: " + uhException.toString());
        }

        Thread pathWatcherWorker = new Thread(fileWatcher);
        pathWatcherWorker.start();
        Socket connection;
        try {
            ServerSocket server = new ServerSocket(PORT);
            while (true) {
                connection = server.accept();
                Thread connectionThread = new Thread(new ConnectionWorker(connection));
                connectionThread.start();
            }
        }
        catch (java.io.IOException ioException){
            System.out.println("Could not create socket :" + ioException.toString());
        }
    }
}

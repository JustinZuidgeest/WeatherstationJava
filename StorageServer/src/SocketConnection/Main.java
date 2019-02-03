package SocketConnection;

import java.net.ServerSocket;
import java.net.Socket;
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

    public static IOWorker ioWorker = new IOWorker();
    private static FileWatcher fileWatcher = new FileWatcher();

    public static void main(String[] args) {
        Thread pathWatcherWorker = new Thread(fileWatcher);
        pathWatcherWorker.start();
        Socket connection;
        try {
            ServerSocket server = new ServerSocket(PORT);
            while (true) {
                System.out.println("Waiting for new connection");
                connection = server.accept();
                System.out.println("New Connection Accepted");
                Thread connectionThread = new Thread(new ConnectionWorker(connection));
                connectionThread.start();
            }
        }
        catch (java.io.IOException ioException){
            System.out.println("Could not create socket :" + ioException.toString());
        }
    }
}

package SocketConnection;

import java.net.ServerSocket;
import java.net.Socket;

import ServerIO.IOWorker;

public class Main {

    private static final int PORT = 54872;
    private static final int maxConnections = 2;
    static Semaphore sem = new Semaphore(maxConnections);

    public static IOWorker ioWorker = new IOWorker();

    public static void main(String[] args) {
        Socket connection;

        try {
            ServerSocket server = new ServerSocket(PORT);
            while (true) {
                System.out.println("Waiting for connections...");
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

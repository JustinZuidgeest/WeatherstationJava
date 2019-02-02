package SocketConnection;

import java.io.*;
import java.net.Socket;

import QueryParser.QueryWorker;

/**
 * ConnectionWorker is the main thread that handles incoming calls for data. It parses the incoming inputstream
 * from a socket as a string (because PHP sends strings through sockets) and processes them accordingly.
 */
public class ConnectionWorker implements Runnable{

    private Socket connection;
    private String returnQuery = null;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;

    ConnectionWorker(Socket connection){
        this.connection = connection;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        }catch (IOException ioException){
            System.out.println("Error creating Buffered Reader or Writer:" + ioException.toString());
        }
    }

    /**
     * The run method of ConnectionWorker constantly looks for input over the socket. Once the PHP script sends a String
     * over the socket the run method will process it based on the String contents. If a request for data is made the
     * run method will create a new QueryWorker thread to handle the query request and terminate the connection after
     * the query has been successfully returned to the sender.
     */
    @Override
    public void run() {
        try{
            //Check the Semaphore to see if there is room for another connection
            Main.sem.attempt();
            String input;
            while(connection.isConnected()){
                //If a String is send over the socket, handle it according to the contents of that String
                if ((bufferedReader.ready() && (input = bufferedReader.readLine()) != null)) {
                    if (input.startsWith("update")) {
                        String[] countries = {"FRANCE", "MEXICO", "UNITED STATES", "SPAIN", "NORTH POLE", "SOUTH POLE"};
                        Thread queryThread = new Thread(new QueryWorker(this, countries , 10));
                        queryThread.start();
                    }else if(input.startsWith("fetch")){
                        try {
                            //Split the fetch command into an array of countries and an integer for the data count
                            String[] arguments = input.split(";");
                            String[] countries = arguments[1].toUpperCase().split(",");
                            int count = Integer.parseInt(arguments[2]);
                            Thread queryThread = new Thread(new QueryWorker(this, countries, count));
                            queryThread.start();
                        }catch (Exception exception){
                            writeOut("Error Parsing Query");
                            System.out.println(exception.toString());
                        }
                    }else if(input.startsWith("hello")){
                        writeOut("Hi");
                    }else if(input.startsWith("close")){
                        break;
                    }else{
                        writeOut("Invalid Request");
                    }
                }
                if (returnQuery != null){
                    System.out.println("Done parsing query, closing connection...");
                    writeOut(returnQuery);
                    break;
                }
            }
            //After a reply has been sent over the socket, close the connection and update the Semaphore
            connection.close();
            System.out.println("Socket was closed...");
            Main.sem.close();
        }catch (InterruptedException ieException){
            System.out.println("Thread interruption error :" + ieException.toString());
        }catch (IOException ioException){
            System.out.println("IO Error 1:" + ioException.toString());
        }
    }

    /**
     * Used by the query parser thread to update the reply that needs to be sent back
     *
     * @param query The String that needs to be sent back over the socket as a reply
     */
    public void setReturnQuery(String query){
        returnQuery = query;
    }

    /**
     * Writes a message to the socket that was used to establish a connection
     *
     * @param outputString The String that needs to be written over the socket
     */
    private void writeOut(String outputString){
        try {
            bufferedWriter.write(outputString + "\r\n");
            bufferedWriter.flush();
        }catch (IOException ioException){
            System.out.println("IO Error :" + ioException.toString());
        }
    }
}
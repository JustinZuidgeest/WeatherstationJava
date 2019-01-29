package SocketConnection;

import java.io.*;
import java.net.Socket;

import QueryParser.QueryWorker;


public class ConnectionWorker implements Runnable{

    private Socket connection;
    private String returnQuery = null;
    private BufferedWriter bufferedWriter;

    ConnectionWorker(Socket connection){
        this.connection = connection;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        }catch (IOException ioException){
            System.out.println("IO Error :" + ioException.toString());
        }
    }

    @Override
    public void run() {
        try{
            Main.sem.attempt();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String input;

            while(connection.isConnected()){
                if ((bufferedReader.ready() && (input = bufferedReader.readLine()) != null)) {
                    if (input.startsWith("update")) {
                        String[] countries = {"FRANCE", "MEXICO", "UNITED STATES", "SPAIN"};
                        Thread queryThread = new Thread(new QueryWorker(this, countries , 10));
                        queryThread.start();
                    }else if(input.startsWith("fetch")){
                        try {
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
                    writeOut(returnQuery);
                    returnQuery = null;
                }
            }
            connection.close();
            System.out.println("Socket was closed...");
            Main.sem.close();
        }catch (InterruptedException ieException){
            System.out.println("Thread interruption error :" + ieException.toString());
        }catch (IOException ioException){
            System.out.println("IO Error :" + ioException.toString());
        }
    }

    public void setReturnQuery(String query){
        returnQuery = query;
    }

    private void writeOut(String outputString){
        try {
            bufferedWriter.write(outputString + "\r\n");
            bufferedWriter.flush();
        }catch (IOException ioException){
            System.out.println("IO Error :" + ioException.toString());
        }
    }
}

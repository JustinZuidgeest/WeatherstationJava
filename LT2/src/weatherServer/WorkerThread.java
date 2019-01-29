package weatherServer;

import weatherIO.WeatherCSVParser;
import weatherXML.WeatherXMLParser;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static java.lang.Thread.yield;

public class WorkerThread implements Runnable {

    private Socket con;
    private ArrayList<ArrayList> data = new ArrayList<>();
    private String datetime;

    public WorkerThread(Socket con) {
        this.con = con;
    }

    public void run() {
        try {
            WeatherServer.sem.attempt();

            DataInputStream streamIn = new DataInputStream(con.getInputStream());
            WeatherXMLParser parser = new WeatherXMLParser();
            byte[] bytes = new byte[4096];
            while (streamIn.available() < 2921 && !con.isBound()) { yield(); }
            while (streamIn.read(bytes) > 0) {
                parser.parseData(new ByteArrayInputStream(bytes));
                data.add(parser.getData());

                datetime = parser.getDateTime().replace(":", "-");
                System.out.println("dt: " + datetime);
                if (datetime.lastIndexOf("00", 16) != -1) {
                    WeatherCSVParser csvParser = new WeatherCSVParser();
                    for (int x=0; x<data.size(); x++) {
                        csvParser.parseChuck(data.remove(0));
                    }
                    WeatherServer.wio.addLines(datetime, csvParser.getCSV());
                }
                while (streamIn.available() < 2921 && !con.isBound()) { yield(); }
            }

            this.con.close();
            WeatherServer.sem.close();
        }
        catch (IOException ioe) { System.out.println("ioe: " + ioe); }
        catch (InterruptedException ie) { System.out.println("ie: " + ie); }
    }
}

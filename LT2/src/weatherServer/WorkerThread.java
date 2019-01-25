package weatherServer;

import weatherIO.WeatherIOParser;
import weatherXML.WeatherMeasurement;
import weatherXML.WeatherXMLParser;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class WorkerThread implements Runnable {

    private Socket con;
    private ArrayList<ArrayList> data = new ArrayList<>();

    public WorkerThread(Socket con) {
        this.con = con;
    }

    public void run() {
        try {
            WeatherServer.sem.attempt();

            DataInputStream streamIn = new DataInputStream(con.getInputStream());
            WeatherXMLParser parser = new WeatherXMLParser();
            byte[] bytes = new byte[4096];

            while ((streamIn.read(bytes)) > 0) {
                parser.parseData(new ByteArrayInputStream(bytes));
                ArrayList<WeatherMeasurement> alwm = parser.getData();
                if (alwm != null) { data.add(alwm); }
                if (data.size() >= 10) {
                    WeatherIOParser ioParser = new WeatherIOParser();
                    for (int x=0; x<10; x++) {
                        ioParser.parseChuck(data.remove(0));
                    }
                    WeatherServer.wio.addQuery(ioParser.getQuery());
                }
            }

            this.con.close();
            WeatherServer.sem.close();
        }
        catch (IOException ioe) { System.out.println("ioe: " + ioe); }
        catch (InterruptedException ie) { System.out.println("ie: " + ie); }
    }
}

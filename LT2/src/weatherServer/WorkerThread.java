package weatherServer;

import weatherIO.WeatherIOParser;
import weatherXML.WeatherCorrection;
import weatherXML.WeatherMeasurement;
import weatherXML.WeatherXMLParser;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class WorkerThread implements Runnable {

    private Socket con;
    private ArrayList<ArrayList> data = new ArrayList<>();
    private HashMap<Integer, WeatherCorrection> correct = new HashMap<>();

    public WorkerThread(Socket con) {
        this.con = con;
    }

    public void run() {
        try {
            WeatherServer.sem.attempt();

            System.out.println("Server has accepted a new connection");

            DataInputStream streamIn = new DataInputStream(con.getInputStream());
            byte[] bytes = new byte[4096];

            streamIn.read(bytes);
            WeatherXMLParser firstParse = new WeatherXMLParser(new ByteArrayInputStream(bytes));
            createCorrections(firstParse.getData());

            while ((streamIn.read(bytes)) > 0) {
                WeatherXMLParser parser = new WeatherXMLParser(new ByteArrayInputStream(bytes), correct);
                data.add(parser.getData());
                this.correct = parser.getCorrection();
                if (data.size() >= 10) {
                    printCorrection();
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

    private void createCorrections(ArrayList<WeatherMeasurement> firstData) {
        data.add(firstData);
        for (WeatherMeasurement wm : firstData) {
            WeatherCorrection wc = new WeatherCorrection(wm);
            correct.put(wc.getStation(), wc);
        }
    }

    private void printCorrection(){
        for(HashMap.Entry<Integer, WeatherCorrection> entry : correct.entrySet()) {
            System.out.println("Station " + entry.getValue().getStation() + " has an exponential average of " + entry.getValue().getTemperature());
        }
    }
}

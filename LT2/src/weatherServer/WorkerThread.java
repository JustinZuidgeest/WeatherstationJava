package weatherServer;

import weatherIO.WeatherCSVParser;
import weatherXML.WeatherXMLParser;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class WorkerThread implements Runnable {

    private Socket con;
    private ArrayList<ArrayList> data = new ArrayList<>();
    private static final byte mark = "?".getBytes()[0];

    public WorkerThread(Socket con) {
        this.con = con;
    }

    public void run() {
        try {
            WeatherServer.sem.attempt();
            BufferedInputStream in = new BufferedInputStream(con.getInputStream());
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            byte[] bytes = new byte[4096];
            int count;
            WeatherXMLParser parser = new WeatherXMLParser();
            //Thread.sleep(4000);
            while ((count = in.read(bytes)) > 0) {
                if (count == 4096) { System.out.println("Ree: "); }
                buf.write(bytes, 0, count);
                parser.parseData(new ByteArrayInputStream(buf.toByteArray()));
                data.add(parser.getData());
                if (data.size() > 10) {
                    WeatherCSVParser csvParser = new WeatherCSVParser();
                    for (int x=0; x<10; x++) {
                        csvParser.parseChuck(data.remove(0));
                    }
                    WeatherServer.wio.addLines(parser.getDateTime().replace(":", "-").substring(0, 16), csvParser.getCSV());
                }
                buf = new ByteArrayOutputStream();
            }

            this.con.close();
            WeatherServer.sem.close();
        }
        catch (IOException ioe) { System.out.println("ioe: " + ioe); }
        catch (InterruptedException ie) { System.out.println("ie: " + ie); }
    }
}

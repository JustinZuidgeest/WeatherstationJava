package weatherServer;

import weatherXML.WeatherMeasurement;
import weatherXML.WeatherXMLParser;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class WorkerThread implements Runnable {

    private byte[] bytes = new byte[3200];
    private byte[] trail = new byte[25];
    private final static byte[] check = {83, 85, 82, 69, 77, 69, 78, 84, 62, 10, 60, 47, 87, 69, 65, 84, 72, 69, 82, 68, 65, 84, 65, 62, 10};
    private ArrayList<ArrayList<WeatherMeasurement>> data = new ArrayList<>();
    private ByteArrayOutputStream buf = new ByteArrayOutputStream();
    private WeatherXMLParser parser = new WeatherXMLParser();
    private Socket con;
    private String datetime;
    private BufferedInputStream in;
    private int count;

    public WorkerThread(Socket con) {
        this.con = con;
    }

    public void run() {
        try {
            WeatherServer.sem.attempt();
            in = new BufferedInputStream(con.getInputStream());
            while ((count = in.read(bytes)) > 0) {
                buf.write(bytes, 0, count);
                while (buf.size() < 3200 && count != -1) {
                    byte[] b = new byte[3200 - buf.size()];
                    count = in.read(b);
                    buf.write(b, 0, count);
                }
                loopTillTarget();
                in.read(trail);
                if (!checkMatchAndLoop()) { continue; }
                buf.write(trail);
                if (count != -1) {
                    parser.parseData(new ByteArrayInputStream(buf.toByteArray()));
                    data.add(parser.getData());
                    datetime = parser.getDateTime();
                    if (datetime.substring(18, 19).equals("9")) {
                        WeatherServer.wio.addLines(parser.getDateTime().substring(0, 16).replace(":", "-"), data);
                        data.clear();
                    }
                    buf = new ByteArrayOutputStream();
                }
            }
            System.out.println("Closing");
            this.con.close();
            WeatherServer.sem.close();
        }
        catch (IOException ioe) { System.out.println("ioe: " + ioe); }
        catch (InterruptedException ie) { System.out.println("ie: " + ie); }
    }

    private void loopTillTarget() {
        try {
            do {
                count = in.read();
                buf.write(count);
            } while (count != 65 && count != -1);
        }
        catch (IOException ioe) { System.out.println("loopTillTarget ioe: " + ioe); }
    }

    private boolean checkMatchAndLoop() {
        try {
            while (!Arrays.equals(trail, check) && count != -1) {
                if (buf.size() < 3300) {
                    loopTillTarget();
                    count = in.read(trail);
                } else {
                    System.out.println("Flushing");
                    byte[] flush = new byte[in.available()];
                    in.read(flush);
                    buf = new ByteArrayOutputStream();
                    return false;
                }
            }
            return true;
        }
        catch (IOException ioe) { System.out.println("checkMatchAndLoop ioe: " + ioe); }
        System.out.println("Nani");
        return true;
    }
}

package weatherIO;

import weatherXML.WeatherMeasurement;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class WeatherIOWorker implements Runnable {

    private String title;
    private ArrayList<ArrayList<WeatherMeasurement>> data;

    public WeatherIOWorker(String title, ArrayList<ArrayList<WeatherMeasurement>> data) {
        this.title = title;
        this.data = data;
    }

    public void run() {
        try {
            File file = new File(title + ".csv");
            FileChannel fc = new RandomAccessFile(file, "rw").getChannel();
            ByteBuffer buffer = fc.map(FileChannel.MapMode.READ_WRITE, 0, 920*data.size());
            for (ArrayList<WeatherMeasurement> al : data) {
                for (WeatherMeasurement wm : al) {
                    buffer.put(wm.toString().getBytes());
                }
            }
            fc.close();
        }
        catch (Exception e) { System.out.println("e: " + e); e.printStackTrace(); }
    }
}

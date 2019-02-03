package weatherIO;

import weatherXML.WeatherCorrection;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WeatherIOMinutes implements Runnable {

    private final static String fs = System.getProperty("file.separator");
    private String title;
    private ArrayList<HashMap<Integer, WeatherCorrection>> data;

    public WeatherIOMinutes(String title, ArrayList<HashMap<Integer, WeatherCorrection>> data) {
        this.title = title;
        this.data = data;
    }

    public void run() {
        try {
            File file = new File("minutes" + fs + title + ".csv");
            FileChannel fc = new RandomAccessFile(file, "rw").getChannel();
            ByteBuffer buffer = fc.map(FileChannel.MapMode.READ_WRITE, 0, 750*data.size());
            for (HashMap<Integer, WeatherCorrection> hm : data) {
                for (Map.Entry<Integer, WeatherCorrection> entry : hm.entrySet()) {
                    buffer.put(entry.getValue().toString().getBytes());
                }
            }
            fc.close();
        }
        catch (Exception e) { System.out.println("e: " + e); e.printStackTrace(); }
    }
}

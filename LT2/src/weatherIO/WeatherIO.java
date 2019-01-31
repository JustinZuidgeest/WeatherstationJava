package weatherIO;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

public class WeatherIO {
    private final static String fs = System.getProperty("file.separator");
    private HashMap<String, StringBuilder> data = new HashMap<>();
    private String last;
    private String current;
    private boolean working = false;

    public WeatherIO() {}

    public void doDaTing() {
        WeatherIOWorker wiow = new WeatherIOWorker(last, data.remove(last));
        Thread worker = new Thread(wiow);
        worker.start();
    }

    public synchronized void addLines(String dt, String q) {
        if (data.putIfAbsent(dt, new StringBuilder()) == null) {
            if (current != null) {
                System.out.println("Going current: " + current);
                last = current;
                doDaTing();
            }
            current = dt;
        }
        data.get(dt).append(q);
        if (working) { notify(); }
    }
}

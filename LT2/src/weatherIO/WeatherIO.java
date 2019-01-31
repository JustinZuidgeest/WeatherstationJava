package weatherIO;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;

public class WeatherIO implements Runnable {
    private final static String fs = System.getProperty("file.separator");
    private HashMap<String, StringBuilder> data = new HashMap<>();
    private String last;
    private String current;
    private boolean working = false;

    public WeatherIO() {}

    public synchronized void run() {
        try {
            while (true) {
                if (working) {
                    File file = new File(last + ".csv");
                    RandomAccessFile raf = new RandomAccessFile(file, "rw");
                    FileChannel fc = raf.getChannel();
                    byte[] b = data.remove(last).toString().getBytes();
                    ByteBuffer buffer = ByteBuffer.allocate(b.length);
                    buffer.put(b);
                    buffer.flip();
                    fc.write(buffer);
                    raf.close();
                    fc.close();
                    working = false;
                }
                wait();
            }
        }
        catch (Exception e) { System.out.println("e: " + e); e.printStackTrace(); }
    }

    public synchronized void addLines(String dt, String q) {
        if (data.putIfAbsent(dt, new StringBuilder()) == null) {
            if (current != null) {
                System.out.println("Going current: " + current);
                last = current;
                working = true;
            }
            current = dt;
        }
        data.get(dt).append(q);
        if (working) { notify(); }
    }
}

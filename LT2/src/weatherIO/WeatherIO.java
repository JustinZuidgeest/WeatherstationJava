package weatherIO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;

public class WeatherIO {
    private final static String fs = System.getProperty("file.separator");
    private HashMap<String, StringBuilder> data = new HashMap<>();
    private boolean working = false;

    public WeatherIO() {}

    public synchronized void writeBatch(String name, String content) {
        try {
            File file = new File(name + ".csv");
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            byte[] b = content.getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(b.length);
            buffer.put(b);
            buffer.flip();
            bw.write(content);

            bw.flush();
            bw.close();
        }
        catch (Exception e) { System.out.println(e); }
    }

    public synchronized void addLines(String dt, String q) {
        data.putIfAbsent(dt, new StringBuilder());
        data.get(dt).append(q);
        System.out.println(data.size());
        if (data.size() > 1) {
            System.out.println("Going down");
            writeBatch(dt, data.remove(dt).toString());
        }
    }
}

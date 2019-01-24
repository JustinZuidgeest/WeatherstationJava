package weatherIO;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class WeatherIO implements Runnable {

    private ArrayList<String> queries = new ArrayList<>();

    public WeatherIO() {}

    public synchronized void run() {
        try {
            while (true) {
                /**
                if (queries.size() > 0) {
                    RandomAccessFile raf = new RandomAccessFile("D:\\outputstream.csv", "rw");
                    FileChannel channel = raf.getChannel();
                    raf.seek(raf.length());
                    while (queries.size() > 0) {
                        String s = queries.remove(0);
                        byte[] b = s.getBytes();
                        ByteBuffer buffer = ByteBuffer.allocate(b.length);
                        buffer.put(b);
                        buffer.flip();
                        channel.write(buffer);
                    }
                    raf.close();
                    channel.close();
                }
                 **/
                System.out.println("Server has written an entry");
                wait();
            }
        }
        catch (Exception e) { System.out.println(e); }
    }

    public synchronized void addQuery(String q) {
        queries.add(q);
        if (queries.size() >= 800) {
            notify();
        }
    }
}

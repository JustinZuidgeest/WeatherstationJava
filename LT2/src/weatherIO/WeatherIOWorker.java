package weatherIO;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class WeatherIOWorker implements Runnable {

    private String title;
    private StringBuilder lines;

    public WeatherIOWorker(String title, StringBuilder lines) {
        this.title = title;
        this.lines = lines;
    }

    public void run() {
        try {
            File file = new File(title + ".csv");
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            FileChannel fc = raf.getChannel();
            byte[] b = lines.toString().getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(b.length);
            buffer.put(b);
            buffer.flip();
            fc.write(buffer);
            raf.close();
            fc.close();
        }
        catch (Exception e) { System.out.println("e: " + e); e.printStackTrace(); }
    }
}

package weatherIO;

import weatherXML.WeatherMeasurement;

import java.util.ArrayList;
import java.util.HashMap;

public class WeatherIO {
    private final static String fs = System.getProperty("file.separator");
    private HashMap<String, ArrayList<ArrayList<WeatherMeasurement>>> data = new HashMap<>();
    private String last;
    private String current;

    public WeatherIO() {}

    public void doDaTing(String name) {
        WeatherIOWorker wiow = new WeatherIOWorker(name, data.remove(name));
        Thread worker = new Thread(wiow);
        worker.start();
    }

    public synchronized void addLines(String dt, ArrayList<ArrayList<WeatherMeasurement>> alwm) {
        if (data.putIfAbsent(dt, new ArrayList<>()) == null) {
            if (current != null) {
                if (last != null) {
                    System.out.println("Going current: " + current);
                    doDaTing(last);
                }
                last = current;
            }
            current = dt;
        }
        data.get(dt).addAll(alwm);
    }
}

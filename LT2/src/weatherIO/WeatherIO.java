package weatherIO;

import weatherXML.WeatherCorrection;
import weatherXML.WeatherMeasurement;

import java.util.ArrayList;
import java.util.HashMap;

public class WeatherIO {
    private HashMap<String, ArrayList<ArrayList<WeatherMeasurement>>> data = new HashMap<>();
    private HashMap<String, ArrayList<HashMap<Integer, WeatherCorrection>>> minutes = new HashMap<>();
    private String last;
    private String current;
    private String lastMinute;
    private String currentMinute;

    public WeatherIO() {}

    public void doRaw(String name) {
        WeatherIOWorker wiow = new WeatherIOWorker(name, data.remove(name));
        Thread worker = new Thread(wiow);
        worker.start();
    }

    public void doMinute(String name) {
        WeatherIOMinutes wiom = new WeatherIOMinutes(name, minutes.remove(name));
        Thread worker = new Thread(wiom);
        worker.start();
    }

    public synchronized void addData(String dt, ArrayList<ArrayList<WeatherMeasurement>> alwm) {
        if (data.putIfAbsent(dt, new ArrayList<>()) == null) {
            if (current != null) {
                if (last != null) {
                    System.out.println("Main IO: " + last);
                    doRaw(last);
                }
                last = current;
            }
            current = dt;
        }
        data.get(dt).addAll(alwm);
    }

    public synchronized void addMinute(String dt, HashMap<Integer, WeatherCorrection> alwm) {
        if (minutes.putIfAbsent(dt, new ArrayList<>()) == null) {
            if (currentMinute != null) {
                if (lastMinute != null) {
                    System.out.println("Minute IO: " + lastMinute);
                    doMinute(lastMinute);
                }
                lastMinute = currentMinute;
            }
            currentMinute = dt;
        }
        minutes.get(dt).add(alwm);
    }
}

package weatherIO;

import weatherXML.WeatherMeasurement;

import java.util.ArrayList;

public class WeatherCSVParser {

    private StringBuilder query = new StringBuilder();

    public WeatherCSVParser() {}

    public void parseChuck(ArrayList<WeatherMeasurement> data) {
        for (WeatherMeasurement entry: data) {
            query.append(entry.getStation());
            query.append(",");
            query.append(entry.getDate());
            query.append(",");
            query.append(entry.getTime());
            query.append(",");
            query.append(entry.getTemperature());
            query.append(",");
            query.append(entry.getDew());
            query.append(",");
            query.append(entry.getAirStation());
            query.append(",");
            query.append(entry.getAirSea());
            query.append(",");
            query.append(entry.getVisibility());
            query.append(",");
            query.append(entry.getWindSpeed());
            query.append(",");
            query.append(entry.getRain());
            query.append(",");
            query.append(entry.getSnow());
            query.append(",");
            query.append(entry.getClouds());
            query.append(",");
            query.append(entry.getWindDegree());
            query.append(",");
            query.append(entry.isRained());
            query.append(",");
            query.append(entry.isSnowed());
            query.append(",");
            query.append(entry.isFrozen());
            query.append(",");
            query.append(entry.isHailed());
            query.append(",");
            query.append(entry.isThunder());
            query.append(",");
            query.append(entry.isTornado());
            query.append("\n");
            // In case of compatibility issues
            // query.append(System.getProperty("line.separator"));
        }
    }

    public String getCSV() {
        return query.toString();
    }
}

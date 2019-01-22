package weatherIO;

import weatherXML.WeatherMeasurement;

import java.util.ArrayList;

public class WeatherIOParser {

    private StringBuilder query = new StringBuilder();

    public WeatherIOParser() {}

    public void parseChuck(ArrayList<WeatherMeasurement> data) {
        for (int x=0; x<10; x++) {
            query.append(data.get(x).getStation());
            query.append(",");
            query.append(data.get(x).getDate());
            query.append(",");
            query.append(data.get(x).getTime());
            query.append(",");
            query.append(data.get(x).getTemperature());
            query.append(",");
            query.append(data.get(x).getDew());
            query.append(",");
            query.append(data.get(x).getAirStation());
            query.append(",");
            query.append(data.get(x).getAirSea());
            query.append(",");
            query.append(data.get(x).getVisibility());
            query.append(",");
            query.append(data.get(x).getWindSpeed());
            query.append(",");
            query.append(data.get(x).getRain());
            query.append(",");
            query.append(data.get(x).getSnow());
            query.append(",");
            query.append(data.get(x).getClouds());
            query.append(",");
            query.append(data.get(x).getWindDegree());
            query.append(",");
            query.append(data.get(x).isRained());
            query.append(",");
            query.append(data.get(x).isSnowed());
            query.append(",");
            query.append(data.get(x).isFrozen());
            query.append(",");
            query.append(data.get(x).isHailed());
            query.append(",");
            query.append(data.get(x).isThunder());
            query.append(",");
            query.append(data.get(x).isTornado());
            query.append("\n");
            // In case of compatibility issues
            // query.append(System.getProperty("line.separator"));
        }
    }

    public String getQuery() {
        return query.toString();
    }
}

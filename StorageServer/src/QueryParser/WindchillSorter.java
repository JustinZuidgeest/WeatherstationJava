package QueryParser;

import java.util.Comparator;

public class WindchillSorter implements Comparator<WeatherMeasurement> {
    @Override
    public int compare(WeatherMeasurement o1, WeatherMeasurement o2) {
        return Float.compare(o1.getWindchill(), o2.getWindchill());
    }
}

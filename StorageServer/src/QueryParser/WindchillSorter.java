package QueryParser;

import java.util.Comparator;

/**
 * WindchillSorter implements the java Comparator interface and can be used to sort the WeatherMeasurement
 * classes by their windchill variable
 */
public class WindchillSorter implements Comparator<WeatherMeasurement> {
    @Override
    public int compare(WeatherMeasurement o1, WeatherMeasurement o2) {
        return Float.compare(o1.getWindchill(), o2.getWindchill());
    }
}

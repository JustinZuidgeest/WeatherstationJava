package QueryParser;

/**
 * WeatherMeasurement holds the measurement data of 1 weatherstation in the form of a class
 */
public class WeatherMeasurement {

    private String station;
    private String location;
    private String country;
    private String temperature;
    private float windchill;
    private String dew;
    private String airStation;
    private String airSea;
    private String visibility;
    private String windSpeed;
    private String rain;
    private String snow;
    private String clouds;
    private String windDegree;

    public WeatherMeasurement(String station, String location, String country, String temperature,
                              float windchill, String dew, String airStation, String airSea, String visibility,
                              String windSpeed, String rain, String snow, String clouds, String windDegree)
    {
        this.station = station;
        this.location = location;
        this.country = country;
        this.temperature = temperature;
        this.windchill = windchill;
        this.dew = dew;
        this.airStation = airStation;
        this.airSea = airSea;
        this.visibility = visibility;
        this.windSpeed = windSpeed;
        this.rain = rain;
        this.snow = snow;
        this.clouds = clouds;
        this.windDegree = windDegree;
    }

    public String getStation() {
        return station;
    }

    public String getLocation() { return location; }

    public String getCountry() { return country; }

    public String getTemperature() {
        return temperature;
    }

    public void setWindchill(Float windchill) { this.windchill = windchill; }

    public Float getWindchill() { return windchill; }

    public String getWindchillString() {
        return String.format("\"%.2f\"", windchill).replace(",", ".");
    }

    public String getDew() {
        return dew;
    }

    public String getAirStation() {
        return airStation;
    }

    public String getAirSea() {
        return airSea;
    }

    public String getVisibility() {
        return visibility;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getRain() {
        return rain;
    }

    public String getSnow() {
        return snow;
    }

    public String getClouds() {
        return clouds;
    }

    public String getWindDegree() {
        return windDegree;
    }
}

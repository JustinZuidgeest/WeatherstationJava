package weatherXML;

public class WeatherCorrection {
    private int station;
    private float temperature;
    private float dew;
    private float airStation;
    private float airSea;
    private float visibility;
    private float windSpeed;
    private float rain;
    private float snow;
    private float clouds;
    private float windDegree;

    private float multiplier = (2f/31f);

    public WeatherCorrection() {
        this.station = 0;
        this.temperature = 0f;
        this.dew = 0f;
        this.airStation = 0f;
        this.airSea = 0f;
        this.visibility = 0f;
        this.windSpeed = 0f;
        this.rain = 0f;
        this.snow = 0f;
        this.clouds = 0f;
        this.windDegree = 0f;
    }

    public WeatherCorrection(WeatherMeasurement wm) {
        this.station = wm.getStation();
        this.temperature = wm.getTemperature();
        this.dew = wm.getDew();
        this.airStation = wm.getAirStation();
        this.airSea = wm.getAirSea();
        this.visibility = wm.getVisibility();
        this.windSpeed = wm.getWindSpeed();
        this.rain = wm.getRain();
        this.snow = wm.getSnow();
        this.clouds = wm.getClouds();
        this.windDegree = wm.getWindDegree();
    }

    public void addMeasurement(WeatherMeasurement wm) {
        this.temperature = this.temperature * (1 - multiplier) + multiplier * wm.getTemperature();
        this.dew = this.dew * (1 - multiplier) + multiplier * wm.getDew();
        this.airStation = this.airStation * (1 - multiplier) + multiplier * wm.getAirStation();
        this.airSea = this.airSea * (1 - multiplier) + multiplier * wm.getAirSea();
        this.visibility = this.visibility * (1 - multiplier) + multiplier * wm.getVisibility();
        this.windSpeed = this.windSpeed * (1 - multiplier) + multiplier * wm.getWindSpeed();
        this.rain = this.rain * (1 - multiplier) + multiplier * wm.getRain();
        this.snow = this.snow * (1 - multiplier) + multiplier * wm.getSnow();
        this.clouds = this.clouds * (1 - multiplier) + multiplier * wm.getClouds();
        this.windDegree = this.windDegree * (1 - multiplier) + multiplier * wm.getWindDegree();
    }

    public int getStation()         { return station; }
    public float getTemperature()   { return temperature; }
    public float getDew()           { return dew; }
    public float getAirStation()    { return airStation; }
    public float getAirSea()        { return airSea; }
    public float getVisibility()    { return visibility; }
    public float getWindSpeed()     { return windSpeed; }
    public float getRain()          { return rain; }
    public float getSnow()          { return snow; }
    public float getClouds()        { return clouds; }
    public float getWindDegree()    { return windDegree; }
}

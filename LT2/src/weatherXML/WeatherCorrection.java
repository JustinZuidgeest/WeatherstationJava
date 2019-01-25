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

    private final static float mp = (2f/31f);
    private int count = 1;


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
        if (count < 30) {
            SimpleAverage(wm);
            return;
        }
        temperature = temperature * (1 - mp) + mp * wm.getTemperature();
        dew = dew * (1 - mp) + mp * wm.getDew();
        airStation = airStation * (1 - mp) + mp * wm.getAirStation();
        airSea = airSea * (1 - mp) + mp * wm.getAirSea();
        visibility = visibility * (1 - mp) + mp * wm.getVisibility();
        windSpeed = windSpeed * (1 - mp) + mp * wm.getWindSpeed();
        rain = rain * (1 - mp) + mp * wm.getRain();
        snow = snow * (1 - mp) + mp * wm.getSnow();
        clouds = clouds * (1 - mp) + mp * wm.getClouds();
        windDegree = windDegree * (1 - mp) + mp * wm.getWindDegree();
    }

    private void SimpleAverage(WeatherMeasurement wm) {
        float n = 1f / (count + 1);
        temperature = temperature * count * n + n * wm.getTemperature();
        dew = dew * count * n + n * wm.getDew();
        airStation = airStation * count * n + n * wm.getAirStation();
        airSea = airSea * count * n + n * wm.getAirSea();
        visibility = visibility * count * n + n * wm.getVisibility();
        windSpeed = windSpeed * count * n + n * wm.getWindSpeed();
        rain = rain * count * n + n * wm.getRain();
        snow = snow * count * n + n * wm.getSnow();
        clouds = clouds * count * n + n * wm.getClouds();
        windDegree = windDegree * count * n + n * wm.getWindDegree();
        count++;
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

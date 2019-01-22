package weatherXML;

public class WeatherMeasurement {

    private int station;
    private String date;
    private String time;
    private float temperature;
    private float dew;
    private float airStation;
    private float airSea;
    private float visibility;
    private float windSpeed;
    private float rain;
    private float snow;
    private byte events;
    private float clouds;
    private int windDegree;

    public WeatherMeasurement() {}

    public int getStation() {
        return station;
    }

    public void setStation(int station) {
        this.station = station;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getDew() {
        return dew;
    }

    public void setDew(float dew) {
        this.dew = dew;
    }

    public float getAirStation() {
        return airStation;
    }

    public void setAirStation(float airStation) {
        this.airStation = airStation;
    }

    public float getAirSea() {
        return airSea;
    }

    public void setAirSea(float airSea) {
        this.airSea = airSea;
    }

    public float getVisibility() {
        return visibility;
    }

    public void setVisibility(float visibility) {
        this.visibility = visibility;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(float windSpeed) {
        this.windSpeed = windSpeed;
    }

    public float getRain() {
        return rain;
    }

    public void setRain(float rain) {
        this.rain = rain;
    }

    public float getSnow() {
        return snow;
    }

    public void setSnow(float snow) {
        this.snow = snow;
    }

    public void setEvents(byte events) { this.events = events; }

    public int isRained() { return (events & 32) == 32 ? 1 : 0; }

    //public void setRained(boolean rained) { this.rained = rained; this.events.}

    public int isSnowed() { return (events & 16) == 16 ? 1 : 0; }

    //public void setSnowed(boolean snowed) { this.snowed = snowed; }

    public int isFrozen() { return (events & 8) == 8 ? 1 : 0; }

    //public void setFrozen(boolean frozen) { this.frozen = frozen; }

    public int isHailed() { return (events & 4) == 4 ? 1 : 0; }

    //public void setHailed(boolean hailed) { this.hailed = hailed; }

    public int isThunder() { return (events & 2) == 2 ? 1 : 0; }

    //public void setThunder(boolean thunder) { this.thunder = thunder; }

    public int isTornado() { return (events & 1) == 1 ? 1 : 0; }

    //public void setTornado(boolean tornado) { this.tornado = tornado; }

    public float getClouds() {
        return clouds;
    }

    public void setClouds(float clouds) {
        this.clouds = clouds;
    }

    public int getWindDegree() {
        return windDegree;
    }

    public void setWindDegree(int windDegree) {
        this.windDegree = windDegree;
    }

}

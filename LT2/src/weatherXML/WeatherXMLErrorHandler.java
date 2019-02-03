package weatherXML;

public class WeatherXMLErrorHandler {

    public WeatherXMLErrorHandler() {}

    public WeatherMeasurement handleEmptyString(byte flag, WeatherMeasurement wm, WeatherCorrection wc) {
        switch (flag) {
            case 4:
                wm.setTemperature(wc.getTemperature());
                break;
            case 5:
                wm.setDew(wc.getDew());
                break;
            case 6:
                wm.setAirStation(wc.getAirStation());
                break;
            case 7:
                wm.setAirSea(wc.getAirSea());
                break;
            case 8:
                wm.setVisibility(wc.getVisibility());
                break;
            case 9:
                wm.setWindSpeed(wc.getWindSpeed());
                break;
            case 10:
                wm.setRain(wc.getRain());
                break;
            case 11:
                wm.setSnow(wc.getSnow());
                break;
            case 12:
                wm.setClouds(wc.getClouds());
                break;
            case 13:
                wm.setWindDegree(Math.round(wc.getWindDegree()));
                break;
            case 14:
                byte b = 0;
                wm.setEvents(b);
                break;
            default:
                break;
        }
        return wm;
    }
}

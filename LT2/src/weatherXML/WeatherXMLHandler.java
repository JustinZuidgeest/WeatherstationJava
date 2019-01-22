package weatherXML;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class WeatherXMLHandler extends DefaultHandler {
    private WeatherMeasurement temp;
    private String current;
    private ArrayList<WeatherMeasurement> list = new ArrayList<>();

    public WeatherXMLHandler() {}

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("MEASUREMENT")) {
            temp = new WeatherMeasurement();
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("MEASUREMENT")) {
            list.add(temp);
        }
        if (qName.equalsIgnoreCase("STN")) {
            temp.setStation(Integer.parseInt(current));
        }
        if (qName.equalsIgnoreCase("DATE")) {
            temp.setDate(current);
        }
        if (qName.equalsIgnoreCase("TIME")) {
            temp.setTime(current);
        }
        if (qName.equalsIgnoreCase("TEMP")) {
            temp.setTemperature(Float.parseFloat(current));
        }
        if (qName.equalsIgnoreCase("DEWP")) {
            temp.setDew(Float.parseFloat(current));
        }
        if (qName.equalsIgnoreCase("STP")) {
            temp.setAirStation(Float.parseFloat(current));
        }
        if (qName.equalsIgnoreCase("SLP")) {
            temp.setAirSea(Float.parseFloat(current));
        }
        if (qName.equalsIgnoreCase("VISIB")) {
            temp.setVisibility(Float.parseFloat(current));
        }
        if (qName.equalsIgnoreCase("WDSP")) {
            temp.setWindSpeed(Float.parseFloat(current));
        }
        if (qName.equalsIgnoreCase("PRCP")) {
            temp.setRain(Float.parseFloat(current));
        }
        if (qName.equalsIgnoreCase("SNDP")) {
            temp.setSnow(Float.parseFloat(current));
        }
        if (qName.equalsIgnoreCase("CLDC")) {
            temp.setClouds(Float.parseFloat(current));
        }
        if (qName.equalsIgnoreCase("WNDDIR")) {
            temp.setWindDegree(Integer.parseInt(current));
        }
        if (qName.equalsIgnoreCase("FRSHTT")) {
            temp.setEvents(Byte.parseByte(current));
        }
    }

    public void characters(char ch[], int start, int length) throws SAXException {
        current = new String(ch, start, length);
    }

}

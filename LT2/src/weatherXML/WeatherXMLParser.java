package weatherXML;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class WeatherXMLParser extends DefaultHandler {

    private ArrayList<WeatherMeasurement> list = new ArrayList<>();
    private WeatherXMLErrorHandler errHandler = new WeatherXMLErrorHandler();
    private ByteArrayInputStream data;
    private WeatherMeasurement temp;
    private String current;
    private HashMap<Integer, WeatherCorrection> cor;

    public WeatherXMLParser(ByteArrayInputStream data) {
        this.data = data;
        parseData();
    }

    public WeatherXMLParser(ByteArrayInputStream data, HashMap<Integer, WeatherCorrection> cor) {
        this.data = data;
        this.cor = cor;
        parseData();
    }

    public void parseData() {
        SAXParserFactory fac = SAXParserFactory.newInstance();
        try {
            SAXParser par = fac.newSAXParser();
            par.parse(data, this);
        }
        catch (ParserConfigurationException pce) {}
        catch (SAXException se) {}
        catch (IOException ioe) {}
    }

    public void startDocument() throws SAXException {}

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("MEASUREMENT")) {
            temp = new WeatherMeasurement();
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        byte flag = -1;
        try {
            switch(qName.toUpperCase()) {
                case "MEASUREMENT":
                    flag = 0;
                    list.add(temp);
                    break;
                case "STN":
                    flag = 1;
                    temp.setStation(Integer.parseInt(current));
                    break;
                case "DATE":
                    flag = 2;
                    temp.setDate(current);
                    break;
                case "TIME":
                    flag = 3;
                    temp.setTime(current);
                    break;
                case "TEMP":
                    flag = 4;
                    if (cor != null) {
                        float fnew = Float.parseFloat(current);
                        float fold = cor.get(temp.getStation()).getTemperature();
                        float diff = fnew - fold;
                        if (diff > 3) {
                            System.out.println("Temperature too high. Value is " + fnew + " while average is " + fold);
                            temp.setTemperature(fold + 3);
                        } else if (diff < -3) {
                            System.out.println("Temperature too low. Value is " + fnew + " while average is " + fold);
                            temp.setTemperature(fold - 3);
                        } else {
                            temp.setTemperature(fnew);
                        }
                    } else {
                        temp.setTemperature(Float.parseFloat(current));
                    }
                    break;
                case "DEWP":
                    flag = 5;
                    temp.setDew(Float.parseFloat(current));
                    break;
                case "STP":
                    flag = 6;
                    temp.setAirStation(Float.parseFloat(current));
                    break;
                case "SLP":
                    flag = 7;
                    temp.setAirSea(Float.parseFloat(current));
                    break;
                case "VISIB":
                    flag = 8;
                    temp.setVisibility(Float.parseFloat(current));
                    break;
                case "WDSP":
                    flag = 9;
                    temp.setWindSpeed(Float.parseFloat(current));
                    break;
                case "PRCP":
                    flag = 10;
                    temp.setRain(Float.parseFloat(current));
                    break;
                case "SNDP":
                    flag = 11;
                    temp.setSnow(Float.parseFloat(current));
                    break;
                case "CLDC":
                    flag = 12;
                    temp.setClouds(Float.parseFloat(current));
                    break;
                case "WNDDIR":
                    flag = 13;
                    temp.setWindDegree(Integer.parseInt(current));
                    break;
                case "FRSHTT":
                    flag = 14;
                    temp.setEvents(Byte.parseByte(current, 2));
                    break;
                default:
                    break;
            }
        }
        catch (NumberFormatException nfe) {
            if (cor != null) {
                temp = errHandler.handleEmptyString(nfe, flag, temp, cor.get(temp.getStation()));
            } else {
                temp = errHandler.handleEmptyString(nfe, flag, temp, new WeatherCorrection());
            }
        }
    }

    public void characters(char ch[], int start, int length) throws SAXException {
        current = new String(ch, start, length);
    }

    class SortByStation implements Comparator<WeatherMeasurement> {
        public int compare(WeatherMeasurement a, WeatherMeasurement b) {
            return a.getStation() - b.getStation();
        }
    }

    public ArrayList<WeatherMeasurement> getData() {
        Collections.sort(list, new SortByStation());
        return list;
    }

    public HashMap<Integer, WeatherCorrection> getCorrection() {
        for (WeatherMeasurement wm : list) {
            cor.get(wm.getStation()).addMeasurement(wm);
        }
        return cor;
    }
}
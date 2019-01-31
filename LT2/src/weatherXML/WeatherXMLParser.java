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
import java.util.HashMap;

public class WeatherXMLParser extends DefaultHandler {

    private WeatherXMLErrorHandler errHandler = new WeatherXMLErrorHandler();
    private ArrayList<WeatherMeasurement> list;
    private WeatherMeasurement temp;
    private String current;
    private HashMap<Integer, WeatherCorrection> correct;
    private String datetime = "";

    public WeatherXMLParser() {}

    public void parseData(ByteArrayInputStream data) {
        SAXParserFactory fac = SAXParserFactory.newInstance();
        try {
            SAXParser par = fac.newSAXParser();
            par.parse(data, this);
        }
        catch (ParserConfigurationException pce) { System.out.println("pce: " + pce); }
        catch (SAXException se) { System.out.println("se: " + se); }
        catch (IOException ioe) { System.out.println("ioe: " + ioe); }
    }

    @Override
    public void startDocument() throws SAXException {
        list = new ArrayList<>();
    }

    @Override
    public void endDocument() throws SAXException {
        setCorrection();
        datetime = temp.getDate() + "_" + temp.getTime();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("MEASUREMENT")) {
            temp = new WeatherMeasurement();
        }
    }

    @Override
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
                    if (correct != null) {
                        float fnew = Float.parseFloat(current);
                        float fold = correct.get(temp.getStation()).getTemperature();
                        float diff = fnew - fold;
                        if (diff > 3) {
                            //System.out.println("Temperature too high. Value is " + fnew + " while average is " + fold);
                            temp.setTemperature(fold + 3);
                        } else if (diff < -3) {
                            //System.out.println("Temperature too low. Value is " + fnew + " while average is " + fold);
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
            if (correct != null && correct.size() > 9) {
                temp = errHandler.handleEmptyString(nfe, flag, temp, correct.get(temp.getStation()));
            } else {
                temp = errHandler.handleEmptyString(nfe, flag, temp, new WeatherCorrection());
            }
        }
        catch (NullPointerException npe) {
            temp = errHandler.handleEmptyString(npe, flag, temp, new WeatherCorrection());
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        current = new String(ch, start, length);
    }

    private void createCorrections() {
        correct = new HashMap<>();
        for (WeatherMeasurement wm : list) {
            WeatherCorrection wc = new WeatherCorrection(wm);
            this.correct.put(wc.getStation(), wc);
        }
    }

    private void setCorrection() {
        try {
            if (correct == null) {
                createCorrections();
            }
            for (WeatherMeasurement wm : list) {
                correct.get(wm.getStation()).addMeasurement(wm);
            }
        }
        catch (NullPointerException npe) {
            System.out.println("NPE: " + npe);
            createCorrections();
        }
    }
    public ArrayList<WeatherMeasurement> getData() { return list; }
    public String getDateTime() { return datetime; }
}
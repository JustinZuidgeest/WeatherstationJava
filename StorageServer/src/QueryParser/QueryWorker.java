package QueryParser;

import SocketConnection.ConnectionWorker;
import SocketConnection.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * The QueryWorker class is responsible for parsing the requested data into a String format to be sent back to the PHP
 * server for processing
 */
public class QueryWorker implements Runnable{

    private ConnectionWorker connectionWorker;
    private String[] countries;
    private int count;

    public QueryWorker(ConnectionWorker connectionWorker, String[] countries, int count){
        this.connectionWorker = connectionWorker;
        this.countries = countries;
        this.count = count;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        parseQuery();
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("Query Thread Done. Estimated execution time: " + estimatedTime + "ms");
    }

    /**
     * Uses the IOWorker instance to retrieve data from a file and creates WeatherMeasurement objects from the data
     */
    private void parseQuery(){
        //Yield thread if IOWorker happens to be updating its most recent data
        while(!Main.ioWorker.getQueryable()){
            Thread.yield();
            System.out.println("Thread yeeting");
        }
        ArrayList<String> csvLines = Main.ioWorker.getUpdateList();
        HashMap<String, HashMap> stationList = Main.ioWorker.getStationList();
        ArrayList<ArrayList> queryMeasurements = new ArrayList<>();

        for (String country : countries) {
            ArrayList<WeatherMeasurement> countryMeasurements = new ArrayList<>();
            for (String line : csvLines) {
                //Split every line of the csv file at the , symbol
                String[] splitLine = line.split(",");
                HashMap<String, String> stationData = stationList.get(splitLine[0]);
                //Find the country associated with the weatherstation
                String stationCountry = stationData.get("CNT");

                //If the weatherstation is inside a country that was requested in the query, make a new WeatherMeasurement object
                if (stationCountry.equals(country)) {
                    String stationName = stationData.get("LOC");
                    float windchill = calculateWindchill(Float.parseFloat(splitLine[3]), Float.parseFloat(splitLine[8]));
                    String[] tempEvents = {splitLine[13], splitLine[14], splitLine[15], splitLine[16], splitLine[17], splitLine[18]};
                    WeatherMeasurement tempMeasurement = new WeatherMeasurement(
                            splitLine[0], stationName, stationCountry, splitLine[1], splitLine[2], splitLine[3],windchill,
                            splitLine[4], splitLine[5], splitLine[6], splitLine[7], splitLine[8], splitLine[9], splitLine[10],
                            splitLine[11], splitLine[12], tempEvents);
                    countryMeasurements.add(tempMeasurement);
                }
            }
            queryMeasurements.add(countryMeasurements);
        }
        //Pass the ArrayList filled with WeatherMeasurement objects for every country to the String parse function
        parseToString(queryMeasurements);
    }

    /**
     * Calculates the windchill based on the JAG/TI method employed by the KNMI
     *
     * @param temperature The temperature in C
     * @param windspeed The windspeed in km/h
     *
     * @return The windchill temperature in C
     */
    private float calculateWindchill(float temperature, float windspeed){
        return (float) (13.12 + (0.6215 * temperature) - (11.37 * (Math.pow(windspeed, 0.16)))
                + (0.3965 * temperature * (Math.pow(windspeed, 0.16))));
    }

    /**
     * Takes the ArrayList filled with WeatherMeasurement objects and turns them into a String to send back to PHP
     * according to the query recieved
     *
     * @param queryMeasurements The ArrayList filled with WeatherMeasurement objects for every country requested
     */
    private void parseToString(ArrayList<ArrayList> queryMeasurements){

        StringBuilder queryBuilder = new StringBuilder();
        String parsedQuery;

        //Create the header line that PHP will use to label the data
        queryBuilder.append("LOCATION,COUNTRY,WINDCHILL,AIRPRESSURE;");

        //Loop for every country present in the ArrayList
        for (ArrayList<WeatherMeasurement> countryMeasurements : queryMeasurements){
            //Convert the ArrayList of a country into an Array for sorting
            WeatherMeasurement[] countryMeasurementsArray = new WeatherMeasurement[countryMeasurements.size()];
            countryMeasurementsArray = countryMeasurements.toArray(countryMeasurementsArray);

            //Sort the measurements of the country by their windchill using the WindchillSorter class
            Arrays.sort(countryMeasurementsArray, new WindchillSorter());
            StringBuilder tempBuilder = new StringBuilder();

            //For every measurement, create a string of the location, country, windchill and airpressure of that measurement
            //seperated by a comma and closed by a ; symbol. The amount of measurement Strings per country is dictated by the
            //count variable passed during creation of this class
            for (int i=0; i < count; i++){
                WeatherMeasurement tempMeasurement = countryMeasurementsArray[i];
                tempBuilder.append(tempMeasurement.getLocation());
                tempBuilder.append(",");
                tempBuilder.append(tempMeasurement.getCountry());
                tempBuilder.append(",");
                String windchillSTR = tempMeasurement.getWindchillString();
                tempBuilder.append(windchillSTR.substring(1, windchillSTR.length() -1));
                tempBuilder.append(",");
                tempBuilder.append(tempMeasurement.getAirStation());
                tempBuilder.append(";");
            }
            queryBuilder.append(tempBuilder.toString());
        }
        //Cut the last ; symbol from the String and send it back to the ConnectionWorker thread that requested the
        //parsed query
        parsedQuery = queryBuilder.toString().substring(0, queryBuilder.length() - 1);
        connectionWorker.setReturnQuery(parsedQuery);
    }
}

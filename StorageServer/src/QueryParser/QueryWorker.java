package QueryParser;

import SocketConnection.ConnectionWorker;
import SocketConnection.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The QueryWorker class is responsible for parsing the requested data into a String format to be sent back to the PHP
 * server for processing
 */
public class QueryWorker implements Runnable{

    private ConnectionWorker connectionWorker;
    private String[] countries;
    private int count;
    private String filepath;

    public QueryWorker(ConnectionWorker connectionWorker, String[] countries, int count, String filepath){
        this.connectionWorker = connectionWorker;
        this.countries = countries;
        this.count = count;
        this.filepath = filepath;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();

        //Check if the filepath is null (and therefore an update query was requested)
        if (filepath == null){
            parseUpdate();
        }else{
            getAvarages();
        }
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println("Query Thread Done. Estimated execution time: " + estimatedTime + "ms\n");
    }

    /**
     * Uses the IOWorker instance to retrieve data from the latest update file and creates WeatherMeasurement objects from the data
     */
    private void parseUpdate(){
        //Yield thread if IOWorker happens to be updating its most recent data
        while(!Main.ioWorker.getQueryable()){
            Thread.yield();
        }
        ArrayList<String> csvLines = Main.ioWorker.getUpdateList();
        System.out.println("Fetched new data. Size: " + csvLines.size());

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
                    float windchill = calculateWindchill(Float.parseFloat(splitLine[1]), Float.parseFloat(splitLine[6]));
                    WeatherMeasurement tempMeasurement = new WeatherMeasurement(
                            splitLine[0], stationName, stationCountry, stationData.get("LAT"), stationData.get("LONG"),
                            splitLine[1],windchill, splitLine[2], splitLine[3], splitLine[4], splitLine[5], splitLine[6],
                            splitLine[7], splitLine[8], splitLine[9], splitLine[10]);
                    countryMeasurements.add(tempMeasurement);
                }
            }
            queryMeasurements.add(countryMeasurements);
        }
        //Pass the ArrayList filled with WeatherMeasurement objects for every country to the String parse function
        parseToString(queryMeasurements);
    }

    /**
     * Calculates the avarages per station from a retrieved file
     */
    private void getAvarages(){
        //Read all the lines of the file into an ArrayList
        ArrayList<String> allLines = Main.ioWorker.readFile(filepath);

        //Notify the PHP script of no data could be extracted for the specified file
        if (allLines == null){
            connectionWorker.setReturnQuery("No Data");
            return;
        }

        HashMap<String, ArrayList<Float[]>> stationReadings = new HashMap<>();

        //Loop through all the lines in the file
        for (String line : allLines){
            String[] splitLine = line.split(",");
            //If the stationID key already exists, add the measurements to an ArrayList inside a hashmap
            if (stationReadings.containsKey(splitLine[0])){
                Float temp = Float.parseFloat(splitLine[1]);
                Float wind = Float.parseFloat(splitLine[2]);
                Float pressure = Float.parseFloat(splitLine[3]);
                Float[] tempFloat = {temp, wind, pressure};

                stationReadings.get(splitLine[0]).add(tempFloat);
            //If the stationID doesn't exist yet, put a new ArrayList in the hashmap using the stationID as key
            }else{
                Float temp = Float.parseFloat(splitLine[1]);
                Float wind = Float.parseFloat(splitLine[2]);
                Float pressure = Float.parseFloat(splitLine[3]);
                Float[] tempFloat = {temp, wind, pressure};

                ArrayList<Float[]> tempArray = new ArrayList<>();
                tempArray.add(tempFloat);

                stationReadings.put(splitLine[0], tempArray);
            }
        }

        //The ArrayList that will be sent to be parsed for the PHP request
        ArrayList<String> historyArray = new ArrayList<>();

        //Loop through the hashmap containing station measurements, calculating avarages per station and appending those
        //avarages to the StringBuilder
        for(Map.Entry<String, ArrayList<Float[]>> entry : stationReadings.entrySet()){
            StringBuilder builder = new StringBuilder();
            int count = 0;
            float totalTemp = 0;
            float totalPressure = 0;
            float totalWind = 0;
            for (Float[] stationReading : entry.getValue()){
                totalTemp += stationReading[0];
                totalWind += stationReading[1];
                totalPressure += stationReading[2];
                count++;
            }
            float avarageTemp = totalTemp / count;
            float avaragePressure = totalPressure / count;
            float avarageWind = totalWind / count;
            builder.append(entry.getKey());
            builder.append(",");
            String tempSTR = String.format("\"%.2f\"", avarageTemp).replace(",", ".");
            builder.append(tempSTR.substring(1, tempSTR.length() -1));
            builder.append(",");
            String windSTR = String.format("\"%.2f\"", avarageWind).replace(",", ".");
            builder.append(windSTR.substring(1, windSTR.length() -1));
            builder.append(",");
            String pressureSTR = String.format("\"%.2f\"", avaragePressure).replace(",", ".");
            builder.append(pressureSTR.substring(1, pressureSTR.length() -1));
            historyArray.add(builder.toString());
        }
        parseHistory(historyArray);
    }

    /**
     * Uses the IOWorker instance to retrieve data from the requested day and creates WeatherMeasurement objects from the data
     */
    private void parseHistory(ArrayList<String> csvLines){
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
                    float windchill = calculateWindchill(Float.parseFloat(splitLine[1]), Float.parseFloat(splitLine[2]));
                    WeatherMeasurement tempMeasurement = new WeatherMeasurement(
                            splitLine[0], stationName, stationCountry, stationData.get("LAT"), stationData.get("LONG"),
                            splitLine[1], windchill, null, splitLine[3], null, null, splitLine[2],
                            null, null, null, null);
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
        queryBuilder.append("LOCATION,COUNTRY,WINDCHILL,AIRPRESSURE,LAT,LONG;");

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
            for (int i=0; i < countryMeasurementsArray.length; i++){
                if (i >= count) break;
                WeatherMeasurement tempMeasurement = countryMeasurementsArray[i];
                tempBuilder.append(tempMeasurement.getLocation());
                tempBuilder.append(",");
                tempBuilder.append(tempMeasurement.getCountry());
                tempBuilder.append(",");
                String windchillSTR = tempMeasurement.getWindchillString();
                tempBuilder.append(windchillSTR.substring(1, windchillSTR.length() -1));
                tempBuilder.append(",");
                tempBuilder.append(tempMeasurement.getAirStation());
                tempBuilder.append(",");
                tempBuilder.append(tempMeasurement.getLat());
                tempBuilder.append(",");
                tempBuilder.append(tempMeasurement.getLng());
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

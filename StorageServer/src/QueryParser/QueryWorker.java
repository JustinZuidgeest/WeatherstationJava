package QueryParser;

import SocketConnection.ConnectionWorker;
import SocketConnection.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
        System.out.println("Estimated execution time: " +estimatedTime);
    }

    private void parseQuery(){
        ArrayList<String> csvLines = Main.ioWorker.readFile("outputstream.csv");
        HashMap<String, HashMap> stationList = Main.ioWorker.getStationList();
        ArrayList<ArrayList> queryMeasurements = new ArrayList<>();

        for (String country : countries) {
            ArrayList<WeatherMeasurement> countryMeasurements = new ArrayList<>();
            for (String line : csvLines) {
                String[] splitLine = line.split(",");
                HashMap<String, String> stationData = stationList.get(splitLine[0]);
                String stationCountry = stationData.get("CNT");

                if (stationCountry.equals(country)) {
                    String stationName = stationData.get("LOC");
                    String[] tempEvents = {splitLine[13], splitLine[14], splitLine[15], splitLine[16], splitLine[17], splitLine[18]};
                    WeatherMeasurement tempMeasurement = new WeatherMeasurement(
                            splitLine[0], stationName, stationCountry, splitLine[1], splitLine[2], splitLine[3], splitLine[4],
                            splitLine[5], splitLine[6], splitLine[7], splitLine[8], splitLine[9], splitLine[10], splitLine[11],
                            splitLine[12], tempEvents);
                    countryMeasurements.add(tempMeasurement);
                }
            }
            queryMeasurements.add(countryMeasurements);
        }
        parseToString(queryMeasurements);
    }

    private void calculateWindchill(){
        //TODO calculate windchill
    }

    private void parseToString(ArrayList<ArrayList> queryMeasurements){

        StringBuilder queryBuilder = new StringBuilder();
        String parsedQuery;

        for (ArrayList<WeatherMeasurement> countryMeasurements : queryMeasurements){
            WeatherMeasurement[] countryMeasurementsArray = new WeatherMeasurement[countryMeasurements.size()];
            countryMeasurementsArray = countryMeasurements.toArray(countryMeasurementsArray);

            Arrays.sort(countryMeasurementsArray, new WindchillSorter());

            StringBuilder tempBuilder = new StringBuilder();

            for (int i=0; i < count; i++){
                WeatherMeasurement tempMeasurement = countryMeasurementsArray[i];
                tempBuilder.append(tempMeasurement.getLocation());
                tempBuilder.append(",");
                tempBuilder.append(tempMeasurement.getCountry());
                tempBuilder.append(",");
                tempBuilder.append(tempMeasurement.getWindchillString());
                tempBuilder.append(",");
                tempBuilder.append(tempMeasurement.getAirStation());
                tempBuilder.append(";");
            }
            queryBuilder.append(tempBuilder.toString());
        }
        parsedQuery = queryBuilder.toString().substring(0, queryBuilder.length() - 1);
        connectionWorker.setReturnQuery(parsedQuery);
    }
}

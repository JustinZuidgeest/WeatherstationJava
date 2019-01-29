package ServerIO;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class IOWorker{

    private HashMap<String, HashMap> stationList = new HashMap<>();

    public IOWorker(){
        generateStationList();
    }

    private void generateStationList(){
        try{
            BufferedReader filereader = new BufferedReader(new FileReader("stations.csv"));
            String line;
            String[] splitString;
            HashMap<String, String> tempMap;
            while((line = filereader.readLine()) != null){
                splitString = line.split(",");
                tempMap = new HashMap<>();
                tempMap.put("LOC", splitString[1]);
                tempMap.put("CNT", splitString[2]);
                tempMap.put("LAT", splitString[3]);
                tempMap.put("LONG", splitString[4]);
                tempMap.put("ELV", splitString[5]);

                stationList.put(splitString[0], tempMap);
            }
            filereader.close();
        }catch(FileNotFoundException fnException){
            System.out.println("Error opening a file: " + fnException.toString());
        }catch(IOException ioException){
            System.out.println("Error reading csv line" + ioException.toString());
        }
    }

    public HashMap<String, HashMap> getStationList(){
        return stationList;
    }

    public synchronized ArrayList<String> readFile(String filepath){
        String line;
        ArrayList<String> dataset = new ArrayList<>();
        try{
            BufferedReader filereader = new BufferedReader(new FileReader(filepath));
            while((line = filereader.readLine()) != null){
                dataset.add(line);
            }
            filereader.close();
        }catch(FileNotFoundException fnException){
            System.out.println("Error opening a file: " + fnException.toString());
        }catch(IOException ioException){
            System.out.println("Error reading csv line" + ioException.toString());
        }
        return dataset;
    }
}

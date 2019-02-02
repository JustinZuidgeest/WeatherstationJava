package ServerIO;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * IOWorker is a singleton class that handles reading and writing to files in a thread-safe manner
 */
public class IOWorker{

    private HashMap<String, HashMap> stationList = new HashMap<>();

    public IOWorker(){
        generateStationList();
    }

    /**
     * This method is called once when the program is start. It generates a HashMap containing HashMaps of every
     * weatherstion that can generate weatherdata. The HashMaps for every individual weatherstation can be accessed
     * using their ID as key.
     */
    private void generateStationList(){
        try{
            BufferedReader filereader = new BufferedReader(new FileReader("stations.csv"));
            String line;
            String[] splitString;
            HashMap<String, String> tempMap;
            //Loop through every line in the stations.csv file and save the data in a temporary Hashmap
            while((line = filereader.readLine()) != null){
                splitString = line.split(",");

                tempMap = new HashMap<>();
                tempMap.put("LOC", splitString[1]);
                tempMap.put("LAT", splitString[3]);
                tempMap.put("LONG", splitString[4]);
                tempMap.put("ELV", splitString[5]);

                //Check if the station is situated on the northern polar circle by checking for Latitude over 64 degrees
                if(Float.parseFloat(splitString[3]) > 64.0){
                    tempMap.put("CNT", "NORTH POLE");
                }
                //Check if the station is situated on the southern polar circle by checking for Latitude under -64 degrees
                else if(Float.parseFloat(splitString[3]) < -64.0){
                    tempMap.put("CNT", "SOUTH POLE");
                }else{
                    tempMap.put("CNT", splitString[2]);
                }

                //Append the temporary hashmap with the weatherstion data to the stationList variable
                stationList.put(splitString[0], tempMap);
            }
            filereader.close();
        }catch(FileNotFoundException fnException){
            System.out.println("Error opening a file: " + fnException.toString());
        }catch(IOException ioException){
            System.out.println("Error reading csv line" + ioException.toString());
        }
    }

    /**
     * Returns the variable that is created at the start of the program
     *
     * @return The Map of all weatherstations as HashMaps
     */
    public HashMap<String, HashMap> getStationList(){
        return stationList;
    }

    /**
     * Opens a file and stores every line in the file as a String in an ArrayList
     *
     * @param filepath  The path to the file that needs to be read
     * @return  An ArrayList containing Strings for every line in the file that was read
     */
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

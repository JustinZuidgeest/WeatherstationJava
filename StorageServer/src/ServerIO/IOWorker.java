package ServerIO;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * IOWorker is a singleton class that handles any file operations in a thread-safe manner
 */
public class IOWorker{

    private HashMap<String, HashMap> stationList = new HashMap<>();
    private ArrayList<String> updateList;
    private volatile boolean queryable = false;

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
            System.out.println("Error reading station file: " + fnException.toString());
        }catch(IOException ioException){
            System.out.println("Error reading csv line" + ioException.toString());
        }
    }

    /**
     * Opens a file and places the contents into an ArrayList
     *
     * @param filepath The path to the file that should be stored as a List
     * @return  The ArrayList containing a String for every line of the read file
     */
    public synchronized ArrayList<String> readFile(String filepath){
        System.out.println("Reading file: " + filepath);
        String line;

        File file = new File(filepath);
        System.out.println("File size is: " + file.length());

        ArrayList<String> dataset = new ArrayList<>();
        try{
            BufferedReader filereader = new BufferedReader(new FileReader(filepath));
            while((line = filereader.readLine()) != null){
                dataset.add(line);
            }
            filereader.close();
        }catch(FileNotFoundException fnException){
            System.out.println("Error opening a file: " + fnException.toString());
            return null;
        }catch(IOException ioException){
            System.out.println("Error reading csv line" + ioException.toString());
            return null;
        }
        return dataset;
    }

    /**
     * Writes a String to an existing file or creates a new one at the specified filepath
     *
     * @param filepath The path of the new file that should be written to
     */
    public synchronized void writeFile(String filepath, String content){
        try {
            FileWriter fileWriter = new FileWriter(filepath, true);
            fileWriter.write(content);
            fileWriter.close();
        }catch (IOException ioException){
            System.out.println("Could not write to file: " + ioException.toString());
        }
    }

    /**
     * Deletes a file from a directory
     *
     * @param filepath The path of the file that should be deleted
     */
    public synchronized void deleteFile(String filepath){
        if (!new File(filepath).delete()){
            System.out.println("File " + filepath + " could not be deleted");
        }else{
            System.out.println("File " + filepath + "  has been successfully deleted");
        }
    }

    /**
     * Opens the latest data for the update call from the website and stores it in a variable for faster retrieval
     * This method is not syncronized because it can only be called by a single instance of the FileWatcher thread
     *
     * @param filepath The path to the file that contains the new data
     */
    public void refreshUpdateList(String filepath){
        System.out.println("Refreshing update list...");
        updateList = readFile(filepath);
    }

    /**
     * Returns the variable that is created at the start of the program
     *
     * @return The Map of all weatherstations as HashMaps
     */
    public synchronized HashMap<String, HashMap> getStationList(){
        return stationList;
    }

    /**
     * Returns the latest cached version of the minute data for the update call that is requested from the
     * nova moda website
     *
     * @return An ArrayList containing a String object for every line of the latest csv data file
     */
    public synchronized ArrayList<String> getUpdateList() { return updateList; }

    public void setQueryable(boolean queryable) { this.queryable = queryable; }

    public boolean getQueryable() { return queryable; }
}

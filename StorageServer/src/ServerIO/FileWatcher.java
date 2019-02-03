package ServerIO;

import SocketConnection.Main;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Watches a filepath for new files being created by the Raspberry Pi server and processes those files in a format that
 * is more easily queryable and more efficient storage-wise
 */
public class FileWatcher implements Runnable {

    private String minutePath = "storage/minute/";
    private String hourPath = "storage/hour/";
    private String dayPath = "storage/day/";

    private String currentHour = "00";
    private String currentDate = null;
    private boolean initialized = false;

    /**
     * Creates the necessary objects to watch a directory and calls the watchDirectory() function that will monitor
     * the path indefinitely as long as the application runs
     */
    @Override
    public void run() {
        //The path we wish to monitor
        Path monitorPath = Paths.get(minutePath);
        try {
            //Create the watchservice instance that will monitor the filepath
            WatchService watchService = FileSystems.getDefault().newWatchService();
            //Register the filepath with the watchservice so that it will notify us when a new file is created
            monitorPath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            //Watch the filepath, blocking the thread untill a change occurs
            watchDirectory(watchService);
        }catch (IOException ioException){
            System.out.println("Error creating watchservice: " + ioException.toString());
        }
    }

    /**
     * Blocks the thread untill a new file is created in the filepath that is being watched. Then handles the change
     * and resets the watchkey to continue blocking the thread untill the next change is detected.
     */
    private void watchDirectory(WatchService watchService){
        //Create a new watchkey object that will store the WatchService event
        WatchKey key;
        //Block the thread untill an event is registered in the key object
        try {
            while ((key = watchService.take()) != null) {
                //Retrieve the List of events that occurred
                for(WatchEvent<?> event : key.pollEvents()){
                    System.out.println("New file detected: " + event.context().toString());
                    //Pass the list of events to the handleUpdate function for processing
                    handleUpdate(event);
                }
                //Reset the watchkey so a new event can be registered
                key.reset();
            }
        }catch (InterruptedException ieException){
            System.out.println("Watchkey thread was interrupted: " + ieException.toString());
        }
    }

    /**
     * Appends the avarage weathermeasurement data that is received every minute to an hourly log. When a new hour starts,
     * it calls the writeToDay function to calculate the avarages of that day and append it to the dayly data log.
     *
     * @param event The event (creation of a file) that was detected by the WatchKey. Contains the filepath of the
     * new file that was created
     */
    private void handleUpdate(WatchEvent<?> event){
        //Extract filepath of newly created file
        String fileName = event.context().toString();
        //Lock the IOWorker so no connectionthreads can access its data and update it with the new file
        Main.ioWorker.setQueryable(false);
        Main.ioWorker.refreshUpdateList(fileName);
        Main.ioWorker.setQueryable(true);

        //Delete the new file after it has been processed
        Main.ioWorker.deleteFile(minutePath + fileName);

        //Extract the date and current hour from the latest data file name
        String[] splitPath = fileName.split("_");
        String fileHour = splitPath[1].split("-")[0];
        String fileDate = splitPath[0];

        //Set the currentHour and currentDate variables if the application is started for the first time
        if (!initialized){
            currentHour = fileHour;
            currentDate = fileDate;
            initialized = true;
        }

        //If the data does not match the current hour, write avarage of hour file to day file and set new current hour
        if (!currentHour.equals(fileHour)) {
            System.out.println("Hours don't match, writing to day");
            writeToDay(currentDate);
            currentDate = fileDate;
            currentHour = fileHour;
        }

        //Append the most recent data to a StringBuilder
        System.out.println("Appending to hour file");
        StringBuilder hourBuilder = new StringBuilder();
        ArrayList<String> lines = Main.ioWorker.getUpdateList();
        for (String line : lines){
            hourBuilder.append(line);
            hourBuilder.append("\n");
        }

        //Write the contens of the StringBuilder to the current hour file
        Main.ioWorker.writeFile(hourPath + currentHour + ".csv", hourBuilder.toString());
    }

    /**
     * Calculates the avarage temperature, windspeed and airpressure for every weatherstation (8000 stations) and appends
     * those avarages to the day they were measured on
     *
     * @param date The date that the data was measured on, used to generate a filename
     */
    private void writeToDay(String date){
        //Read all the lines of the file into an ArrayList
        ArrayList<String> hourLines = Main.ioWorker.readFile(hourPath + currentHour + ".csv");
        HashMap<String, ArrayList<Float[]>> stationReadings = new HashMap<>();

        //Loop through all the lines in the file
        for (String line : hourLines){
            String[] splitLine = line.split(",");
            //If the stationID key already exists, add the measurements to an ArrayList inside a hashmap
            if (stationReadings.containsKey(splitLine[0])){
                Float temp = Float.parseFloat(splitLine[3]);
                Float wind = Float.parseFloat(splitLine[8]);
                Float pressure = Float.parseFloat(splitLine[5]);
                Float[] tempFloat = {temp, pressure, wind};

                stationReadings.get(splitLine[0]).add(tempFloat);
            //If the stationID doesn't exist yet, put a new ArrayList in the hashmap using the stationID as key
            }else{
                Float temp = Float.parseFloat(splitLine[3]);
                Float wind = Float.parseFloat(splitLine[8]);
                Float pressure = Float.parseFloat(splitLine[5]);
                Float[] tempFloat = {temp, pressure, wind};

                ArrayList<Float[]> tempArray = new ArrayList<>();
                tempArray.add(tempFloat);

                stationReadings.put(splitLine[0], tempArray);
            }
        }

        //The Stringbuilder that will contain the contents of the to-be-created file
        StringBuilder fileContent = new StringBuilder();

        //Loop through the hashmap containing station measurements, calculating avarages per station and appending those
        //avarages to the StringBuilder
        for(Map.Entry<String, ArrayList<Float[]>> entry : stationReadings.entrySet()){
            int count = 0;
            float totalTemp = 0;
            float totalPressure = 0;
            float totalWind = 0;
            for (Float[] stationReading : entry.getValue()){
                totalTemp += stationReading[0];
                totalPressure += stationReading[1];
                totalWind += stationReading[2];
                count++;
            }
            float avarageTemp = totalTemp / count;
            float avaragePressure = totalPressure / count;
            float avarageWind = totalWind / count;
            fileContent.append(entry.getKey());
            fileContent.append(",");
            String tempSTR = String.format("\"%.2f\"", avarageTemp).replace(",", ".");
            fileContent.append(tempSTR.substring(1, tempSTR.length() -1));
            fileContent.append(",");
            String windSTR = String.format("\"%.2f\"", avarageWind).replace(",", ".");
            fileContent.append(windSTR.substring(1, windSTR.length() -1));
            fileContent.append(",");
            String pressureSTR = String.format("\"%.2f\"", avaragePressure).replace(",", ".");
            fileContent.append(pressureSTR.substring(1, pressureSTR.length() -1));
            fileContent.append("\n");
        }

        System.out.println("Writing to day file: " + dayPath + date + ".csv");
        //Write the contents of the StringBuilder to the new file
        Main.ioWorker.writeFile(dayPath + date + ".csv", fileContent.toString());

        System.out.println("Deleting old hour file: " + hourPath + currentHour + ".csv");
        //Delete the old day file after it has been processed
        Main.ioWorker.deleteFile(hourPath + currentHour + ".csv");
    }
}

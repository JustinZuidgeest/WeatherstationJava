package ServerIO;

import SocketConnection.Main;

import java.io.IOException;
import java.nio.file.*;

/**
 * Watches a filepath for new files being created by the Raspberry Pi server and processes those files in a format that
 * is more easily queryable and more efficient storage-wise
 */
public class PathWatcher implements Runnable {

    /**
     * Creates the necessary objects to watch a directory and calls the watchDirectory() function that will monitor
     * the path indefinitely as long as the application runs
     */
    @Override
    public void run() {
        //The path we wish to monitor
        Path monitorPath = Paths.get("storage/minute");
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

    private void handleUpdate(WatchEvent<?> event){
        //Lock the IOWorker so no connectionthreads access its data while it is being updated
        Main.ioWorker.setQueryable(false);
        //Update the ArrayList of IOWorker to store the latest dataset from the new file
        Main.ioWorker.refreshUpdateList(event.context().toString());
        //Release the lock on IOWorker so the connectionthreads can access its data again
        Main.ioWorker.setQueryable(true);

        
    }
}


package eps.scp;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class LoadInvertedIndex implements Runnable{
    private final String DIndexFilePrefix = "IndexFile";
    private ArrayList<Thread> threadList = new ArrayList<>();
    private ArrayList <RecorrerFicheroDeLoad> taskList = new ArrayList<>();
    private String inputDirectory;
    private ConcurrentSkipListMap<String, ConcurrentSkipListSet<Location>> Hash;
    public LoadInvertedIndex(String indexDirectory, ConcurrentSkipListMap<String, ConcurrentSkipListSet<Location>> Hash){
        this.inputDirectory = indexDirectory;
        this.Hash = Hash;
    }
    @Override
    public void run() {
        loadInvertedIndex(inputDirectory);
        for (int i = 0; i < threadList.size(); i++){
            try {
                threadList.get(i).join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void loadInvertedIndex(String inputDirectory){
        File folder = new File(inputDirectory);
        File[] listOfFiles = folder.listFiles((d, name) -> name.startsWith(DIndexFilePrefix));

        // Recorremos todos los ficheros del directorio de Indice y los procesamos.
        int actualTaskNumber = 0;
        for (File file : listOfFiles) {
            if (file.isFile()) {
                taskList.add(new RecorrerFicheroDeLoad(file, Hash));
                threadList.add(Thread.startVirtualThread(taskList.get(actualTaskNumber)));
            }
            actualTaskNumber++;
        }
    }
}


package eps.scp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

public class SaveFilesLines implements Runnable{
    private final String DFileLinesName = "FilesLinesContent";
    private String outputDirectory;
    private ConcurrentSkipListMap<Location, String> IndexFilesLines;
    public SaveFilesLines(String indexDirectory, ConcurrentSkipListMap <Location, String> IndexFilesLines){
        this.outputDirectory = indexDirectory;
        this.IndexFilesLines = IndexFilesLines;
    }
    @Override
    public void run() {
        try {
            File KeyFile = new File(outputDirectory + "/" + DFileLinesName);
            FileWriter fw = new FileWriter(KeyFile);
            BufferedWriter bw = new BufferedWriter(fw);
            Set<Map.Entry<Location, String>> keySet = IndexFilesLines.entrySet();
            Iterator keyIterator = keySet.iterator();

            while (keyIterator.hasNext() )
            {
                Map.Entry<Location, String> entry = (Map.Entry<Location, String>) keyIterator.next();
                bw.write(entry.getKey() + "\t" + entry.getValue() + "\n");
            }
            bw.close(); // Cerramos el fichero.
        } catch (IOException e) {
            System.err.println("Error creating FilesLines contents file: " + outputDirectory + DFileLinesName + "\n");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}

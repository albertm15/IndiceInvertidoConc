/* ---------------------------------------------------------------
Práctica 1.
Código fuente: BarahonaMartinPRA1
Grau Informàtica
49381774S Albert Martín López.
49380060A Pau Barahona Setó.
--------------------------------------------------------------- */
package eps.scp;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

public class SaveFilesIds implements Runnable{
    private final String DFilesIdsName = "FilesIds";
    private String outputDirectory;
    private ConcurrentSkipListMap<Integer,String> Files;
    public SaveFilesIds(String indexDirectory, ConcurrentSkipListMap<Integer,String> Files){
        this.outputDirectory = indexDirectory;
        this.Files = Files;
    }
    @Override
    public void run() {
        try {
            //File IdsFile = new File(outputDirectory +"/"+ DFilesIdsName);
            FileWriter fw = new FileWriter(outputDirectory + "/" + DFilesIdsName);
            BufferedWriter bw = new BufferedWriter(fw);
            Set<Map.Entry<Integer,String>> keySet = Files.entrySet();
            Iterator keyIterator = keySet.iterator();

            while (keyIterator.hasNext() )
            {
                Map.Entry<Integer,String> entry = (Map.Entry<Integer,String>) keyIterator.next();
                bw.write(entry.getKey() + "\t" + entry.getValue() + "\n");
            }
            bw.close(); // Cerramos el fichero.

        } catch (IOException e) {
            System.err.println("Error creating FilesIds file: " + outputDirectory + DFilesIdsName + "\n");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}

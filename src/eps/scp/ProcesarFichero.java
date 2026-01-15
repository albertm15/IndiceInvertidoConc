
package eps.scp;
import java.io.*;
import java.text.Normalizer;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class ProcesarFichero implements Runnable{
    private int fileId;
    private File file;
    private ConcurrentSkipListMap<String, ConcurrentSkipListSet<Location>> Hash;

    private ConcurrentSkipListMap<Location, String> IndexFilesLines;
    private long TotalLines = 0;
    private long TotalWords = 0;
    private long TotalLocations = 0;


    public ProcesarFichero (int fileId, File file, ConcurrentSkipListMap<String, ConcurrentSkipListSet<Location>> Hash, ConcurrentSkipListMap<Location, String> IndexFilesLines){
        this.fileId = fileId;
        this.file = file;
        this.Hash = Hash;
        this.IndexFilesLines = IndexFilesLines;
    }

    @Override
    public void run(){
        addFileWords2Index(fileId, file);
    }

    public void addFileWords2Index(int fileId, File file)
    {
        System.out.printf("Processing %3dth file %s\n", fileId, file.getName());

        // Crear buffer reader para leer el fichero a procesar.
        try(BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String line;
            int lineNumber = 0;  // inicializa contador de líneas a 0.
            while( (line = br.readLine()) !=null)   // Leemos siguiente línea de texto del fichero.
            {
                lineNumber++;
                TotalLines++;
                if (Indexing.Verbose) System.out.printf("Procesando linea %d fichero %d: ",lineNumber,fileId);
                Location newLocation = new Location(fileId, lineNumber);
                addIndexFilesLine(newLocation, line);
                // Eliminamos carácteres especiales de la línea del fichero.
                line = Normalizer.normalize(line, Normalizer.Form.NFD);
                line = line.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
                String filter_line = line.replaceAll("[^a-zA-Z0-9áÁéÉíÍóÓúÚäÄëËïÏöÖüÜñÑ ]","");
                // Dividimos la línea en palabras.
                String[] words = filter_line.split("\\W+");
                //String[] words = line.split("(?U)\\p{Space}+");
                // Procesar cada palabra
                for (String word : words) {
                    if (Indexing.Verbose) System.out.printf("%s ", word);
                    word = word.toLowerCase();

                    while (true) {
                        ConcurrentSkipListSet<Location> locations = Hash.get(word);
                        if (locations == null) {   // Si no existe esa palabra en el indice invertido, creamos una lista vacía de Localizaciones y la añadimos al Indice
                            locations = new ConcurrentSkipListSet<>();
                            if (Hash.putIfAbsent(word, locations) == null) {
                                locations.add(newLocation);
                                TotalLocations++;

                                TotalWords++;
                                break;

                            }
                        } else {
                            if(!locations.contains(newLocation)){
                                locations.add(newLocation);
                                TotalLocations++;
                            }
                            break;
                        }
                        // Añadimos nueva localización en la lista de localizaciomes asocidada con ella.
                    }


                }

                if (Indexing.Verbose) System.out.println();
            }
        } catch (FileNotFoundException e) {
            System.err.printf("Fichero %s no encontrado.\n",file.getAbsolutePath());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.printf("Error lectura fichero %s.\n",file.getAbsolutePath());
            e.printStackTrace();
        }
    }

    private void addIndexFilesLine(Location loc, String line){
        IndexFilesLines.put(loc, line);
    }

    public long getTotalLines() {
        return TotalLines;
    }

    public long getTotalLocations() {
        return TotalLocations;
    }

    public long getTotalWords() {
        return TotalWords;
    }
}


/* ---------------------------------------------------------------
Práctica 1.
Código fuente: BarahonaMartinPRA1
Grau Informàtica
49381774S Albert Martín López.
49380060A Pau Barahona Setó.
--------------------------------------------------------------- */
package eps.scp;
import java.io.File;
import java.util.List;
import java.util.concurrent.*;
import java.util.ArrayList;

public class ProcesarDirectorio implements Runnable{

    private String path;
    private List<File> FilesList;
    private ConcurrentSkipListMap<Integer,String> Files;
    private int TotalFiles = 0;
    private long TotalLines = 0;
    private long TotalWords = 0;
    private long TotalLocations = 0;
    private ArrayList <Thread> threadList = new ArrayList<>();
    private ArrayList <ProcesarFichero> taskList = new ArrayList<>();

    private int fileId = 0;

    private ConcurrentSkipListMap<String, ConcurrentSkipListSet<Location>> Hash;

    private ConcurrentSkipListMap<Location, String> IndexFilesLines;

    protected ProcesarDirectorio(String path, List<File> FilesList, ConcurrentSkipListMap<Integer,String> Files, ConcurrentSkipListMap<String, ConcurrentSkipListSet<Location>> Hash, ConcurrentSkipListMap<Location, String> IndexFilesLines) {
        this.path = path;
        this.FilesList = FilesList;
        this.Files = Files;
        this.Hash = Hash;
        this.IndexFilesLines = IndexFilesLines;
    }

    @Override
    public void run(){
        TotalFiles = 0;
        searchDirectoryFiles(path);

        for(int i = 0; i < threadList.size(); i++){
            try {
                threadList.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for(int i = 0; i < taskList.size(); i++){
            TotalLines += taskList.get(i).getTotalLines();
            TotalWords += taskList.get(i).getTotalWords();
            TotalLocations += taskList.get(i).getTotalLocations();
        }
    }

    public void searchDirectoryFiles(String dirpath){
        File file=new File(dirpath);
        File content[] = file.listFiles();
        if (content != null) {
            for (int i = 0; i < content.length; i++) {
                if (content[i].isDirectory()) {
                    // Si es un directorio, procesarlo recursivamente.
                    searchDirectoryFiles(content[i].getAbsolutePath());
                }
                else {
                    // Si es un fichero de texto, añadirlo a la lista para su posterior procesamiento.
                    if (checkFile(content[i].getName())){
                        FilesList.add(content[i]);
                        fileId++;
                        Files.put(fileId, content[i].getAbsolutePath());
                        taskList.add(new ProcesarFichero(fileId, content[i], Hash, IndexFilesLines));
                        threadList.add(Thread.startVirtualThread(taskList.get(TotalFiles)));
                        TotalFiles++;

                    }
                }
            }
        }
        else
            System.err.printf("Directorio %s no existe.\n",file.getAbsolutePath());
    }

    private boolean checkFile (String name)
    {
        if (name.endsWith("txt")) {
            return true;
        }
        return false;
    }

    public int getTotalFiles(){
        return this.TotalFiles;
    }

    public long getTotalLines() {
        return this.TotalLines;
    }

    public long getTotalLocations() {
        return this.TotalLocations;
    }

    public long getTotalWords() {
        return this.TotalWords;
    }
}

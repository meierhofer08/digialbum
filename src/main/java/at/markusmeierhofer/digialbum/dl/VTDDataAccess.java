package at.markusmeierhofer.digialbum.dl;

import at.markusmeierhofer.digialbum.VTDEntry;
import at.markusmeierhofer.digialbum.bl.config.VTDConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by MM on 12.02.2017.
 */
public class VTDDataAccess {
    private static final class InstanceHolder {
        static final VTDDataAccess INSTANCE = new VTDDataAccess();
    }

    public static VTDDataAccess getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static final Logger LOGGER = LogManager.getLogger();

    private VTDConfig config;

    private VTDDataAccess() {
        config = VTDConfig.getInstance();
    }

    public List<VTDEntry> loadData() throws DataFileNotFoundException {
        List<VTDEntry> entries = null;
        try {
            //noinspection unchecked
            entries = (ArrayList<VTDEntry>) new ObjectInputStream(new FileInputStream(new File(config.getDataFilename()))).readObject();
            if (entries != null) {
                entries.forEach(VTDEntry::read);
            }
        } catch (FileNotFoundException e) {
            LOGGER.catching(e);
            throw new DataFileNotFoundException();
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.catching(e);
        }
        return entries;
    }

    public void saveData(List<VTDEntry> entries) {
        try {
            writeDataFile(entries);
            if (config.isGenerateTextFile()) {
                writeTextFile(entries);
            }
        } catch (IOException e) {
            LOGGER.catching(e);
        }
    }

    private void writeDataFile(List<VTDEntry> entries) throws IOException {
        Path dataPath = Paths.get(config.getDataFilename());
        if (Files.exists(dataPath)) {
            Files.copy(dataPath, Paths.get(dataPath.toAbsolutePath().toString() + "_bkp"), StandardCopyOption.REPLACE_EXISTING);
        }
        FileOutputStream fos = new FileOutputStream(new File(config.getDataFilename()));
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        if (entries != null) {
            entries.forEach(VTDEntry::save);
            oos.writeObject(new ArrayList<>(entries));
        }
        oos.close();
        fos.close();
    }

    private void writeTextFile(List<VTDEntry> entries) throws IOException {
        Path backupPath = Paths.get(config.getBackupFilename());
        if (Files.exists(backupPath)) {
            Files.copy(backupPath, Paths.get(backupPath.toAbsolutePath().toString() + "_bkp"), StandardCopyOption.REPLACE_EXISTING);
        }
        FileWriter fw = new FileWriter(config.getBackupFilename());
        if (entries != null) {
            for (VTDEntry entry : entries) {
                fw.write(entry.getHeader().getValueSafe() + ";");
                fw.write(entry.getImageUrl().getValueSafe() + ";");
                fw.write(entry.getText().getValueSafe() + "#");
            }
            fw.flush();
        }
        fw.close();
    }
}

package at.markusmeierhofer.digialbum;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private VTDConfig config;

    private VTDDataAccess() {
        config = VTDConfig.DEFAULT;
    }

    public List<VTDEntry> loadData() {
        List<VTDEntry> entries = null;
        try {
            entries = (ArrayList<VTDEntry>) new ObjectInputStream(new FileInputStream(new File(config.getDataFilename()))).readObject();
            if (entries != null) {
                entries.forEach(VTDEntry::read);
            }
        } catch (FileNotFoundException e) {
            entries = new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public void saveData(List<VTDEntry> entries) {
        try {
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
            // write backup
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

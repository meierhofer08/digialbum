package at.markusmeierhofer.digialbum;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class VTDConfig {

    private final String dataFilename;
    private final String backupFilename;
    private final String settingsFilename;
    private final String basePath;
    private final boolean useAnimations;

    public VTDConfig(String dataFilename, String backupFilename, String settingsFilename, String basePath, boolean useAnimations) {
        this.dataFilename = dataFilename;
        this.backupFilename = backupFilename;
        this.settingsFilename = settingsFilename;
        this.basePath = basePath;
        this.useAnimations = useAnimations;
    }

    public static VTDConfig DEFAULT = getDefaultConfig();

    private static VTDConfig getDefaultConfig() {
        String settingsName = getDefaultSettingsName();
        try (FileReader fileReader = new FileReader(settingsName)) {
            Properties vtdProperties = new Properties();
            vtdProperties.load(fileReader);
            String basePathProperty = "vtd.basePath";
            String basePath = vtdProperties.getProperty(basePathProperty);
            if (basePath == null) {
                System.out.println(basePathProperty + " property not found, using default(C:\\VTD\\)!");
            }
            String useAnimationsProperty = "vtd.useAnimations";
            String useAnimations = vtdProperties.getProperty(useAnimationsProperty);
            if (useAnimations == null) {
                System.out.println(useAnimationsProperty + " property not found, using default (true)!");
            }

            return new VTDConfig(basePath + "VTDData.dat", basePath + "VTDData.txt",
                    settingsName, basePath, Boolean.parseBoolean(useAnimations));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error when loading VTDSettings!");
        }
    }

    private static String getDefaultSettingsName() {
        String settingsName = "VTDSettings.properties";
        if (!Files.exists(Paths.get(settingsName))) {
            settingsName = "C:\\VTD\\" + settingsName;
            if (!Files.exists(Paths.get(settingsName))) {
                throw new RuntimeException("Could not find VTDSettings in default folders!");
            }
        }

        return settingsName;
    }

    public String getDataFilename() {
        return dataFilename;
    }

    public String getBackupFilename() {
        return backupFilename;
    }

    public String getSettingsFilename() {
        return settingsFilename;
    }

    public String getBasePath() {
        return basePath;
    }

    public boolean isUseAnimations() {
        return useAnimations;
    }
}

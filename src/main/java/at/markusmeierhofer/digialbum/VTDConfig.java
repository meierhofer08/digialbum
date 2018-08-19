package at.markusmeierhofer.digialbum;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class VTDConfig {
    private final static String BASE_PATH_PROPERTY = "vtd.basePath";
    private final static String USE_ANIMATIONS_PROPERTY = "vtd.useAnimations";
    private final static String DATA_FILE = "VTDData.dat";
    private final static String BACKUP_FILE = "VTDData.txt";
    private final static String SETTINGS_FILE = "VTDSettings.properties";
    private final static String DEFAULT_BASE_PATH = "C:\\VTD\\";

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
        if (settingsName == null) {
            return new VTDConfig(DEFAULT_BASE_PATH + DATA_FILE, DEFAULT_BASE_PATH + BACKUP_FILE,
                    SETTINGS_FILE, DEFAULT_BASE_PATH, true);
        }
        try (FileReader fileReader = new FileReader(settingsName)) {
            Properties vtdProperties = new Properties();
            vtdProperties.load(fileReader);
            String basePath = vtdProperties.getProperty(BASE_PATH_PROPERTY);
            if (basePath == null) {
                System.out.println(BASE_PATH_PROPERTY + " property not found, using default(" + DEFAULT_BASE_PATH + ")!");
                basePath = DEFAULT_BASE_PATH;
            }
            String useAnimations = vtdProperties.getProperty(USE_ANIMATIONS_PROPERTY);
            if (useAnimations == null) {
                System.out.println(USE_ANIMATIONS_PROPERTY + " property not found, using default (true)!");
                useAnimations = "true";
            }

            return new VTDConfig(basePath + DATA_FILE, basePath + BACKUP_FILE,
                    settingsName, basePath, Boolean.parseBoolean(useAnimations));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error when loading VTDSettings!");
        }
    }

    private static String getDefaultSettingsName() {
        String settingsName = SETTINGS_FILE;
        if (!Files.exists(Paths.get(settingsName))) {
            settingsName = DEFAULT_BASE_PATH + settingsName;
            if (!Files.exists(Paths.get(settingsName))) {
                System.out.println("Could not find VTDSettings in default folders, using default settings!");
                return null;
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

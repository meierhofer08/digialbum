package at.markusmeierhofer.digialbum;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class VTDConfig {
    private static class InstanceHolder {
        private static final VTDConfig INSTANCE = getDefaultConfig();
    }

    public static VTDConfig getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static final String DATA_FILE = "VTDData.dat";
    private static final String BACKUP_FILE = "VTDData.txt";
    private static final String SETTINGS_FILE = "VTDSettings.properties";
    private static final String DEFAULT_BASE_PATH = "C:\\VTD\\";
    private static final boolean DEFAULT_USE_ANIMATIONS = true;
    private static final String DEFAULT_HEADER_TEXT = "Unsere Geschichte";
    // Settings properties
    private static final String BASE_PATH_PROPERTY = "vtd.basePath";
    private static final String USE_ANIMATIONS_PROPERTY = "vtd.useAnimations";
    private static final String HEADER_TEXT_PROPERTY = "vtd.headerText";

    private static final Logger LOGGER = LogManager.getLogger();

    private final String dataFilename;
    private final String backupFilename;
    private final String settingsFilename;
    private final String basePath;
    private final boolean useAnimations;
    private final String headerText;

    public VTDConfig(String dataFilename, String backupFilename, String settingsFilename, String basePath, boolean useAnimations, String headerText) {
        this.dataFilename = dataFilename;
        this.backupFilename = backupFilename;
        this.settingsFilename = settingsFilename;
        this.basePath = basePath;
        this.useAnimations = useAnimations;
        this.headerText = headerText;
    }

    private static VTDConfig getDefaultConfig() {
        String settingsName = getDefaultSettingsName();
        if (settingsName == null) {
            return new VTDConfig(DEFAULT_BASE_PATH + DATA_FILE, DEFAULT_BASE_PATH + BACKUP_FILE,
                    SETTINGS_FILE, DEFAULT_BASE_PATH, DEFAULT_USE_ANIMATIONS, DEFAULT_HEADER_TEXT);
        }
        try (FileReader fileReader = new FileReader(settingsName)) {
            Properties vtdProperties = new Properties();
            vtdProperties.load(fileReader);
            String basePath = vtdProperties.getProperty(BASE_PATH_PROPERTY);
            if (basePath == null) {
                LOGGER.warn(BASE_PATH_PROPERTY + " property not found, using default(" + DEFAULT_BASE_PATH + ")!");
                basePath = DEFAULT_BASE_PATH;
            }
            String useAnimationsString = vtdProperties.getProperty(USE_ANIMATIONS_PROPERTY);
            boolean useAnimations;
            if (useAnimationsString == null) {
                LOGGER.warn(USE_ANIMATIONS_PROPERTY + " property not found, using default (" +
                        DEFAULT_USE_ANIMATIONS + ")!");
                useAnimations = DEFAULT_USE_ANIMATIONS;
            } else {
                useAnimations = Boolean.parseBoolean(useAnimationsString);
            }
            String headerText = vtdProperties.getProperty(HEADER_TEXT_PROPERTY);
            if (headerText == null) {
                LOGGER.warn(HEADER_TEXT_PROPERTY + " property not found, using default (" +
                        DEFAULT_HEADER_TEXT + ")!");
                headerText = DEFAULT_HEADER_TEXT;
            }

            return new VTDConfig(basePath + DATA_FILE, basePath + BACKUP_FILE,
                    settingsName, basePath, useAnimations, headerText);
        } catch (IOException e) {
            LOGGER.catching(e);
            throw new RuntimeException("Error when loading VTDSettings!");
        }
    }

    private static String getDefaultSettingsName() {
        String settingsName = SETTINGS_FILE;
        if (!Files.exists(Paths.get(settingsName))) {
            settingsName = DEFAULT_BASE_PATH + settingsName;
            if (!Files.exists(Paths.get(settingsName))) {
                LOGGER.warn("Could not find VTDSettings in default folders, using default settings!");
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

    public String getHeaderText() {
        return headerText;
    }
}

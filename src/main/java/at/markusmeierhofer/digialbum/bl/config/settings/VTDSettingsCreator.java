package at.markusmeierhofer.digialbum.bl.config.settings;

import at.markusmeierhofer.digialbum.bl.config.StringKey;
import at.markusmeierhofer.digialbum.bl.config.VTDConfig;
import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;

public class VTDSettingsCreator {
    private static final Logger LOGGER = LogManager.getLogger();

    private static boolean guiAvailable = false;

    public static void setGuiAvailable(boolean guiAvailable) {
        VTDSettingsCreator.guiAvailable = guiAvailable;
    }

    public static void createDefaultSettings(String settingsName, String settingsContent) {
        LOGGER.warn("Could not find VTDSettings in default folders, creating default settings file " +
                "in current folder!");
        try (FileWriter fileWriter = new FileWriter(settingsName)) {
            fileWriter.write(settingsContent);
        } catch (IOException e) {
            LOGGER.fatal(e);
            throw new RuntimeException("Could not write default settings: " + e.getMessage());
        }

        if (guiAvailable) {
            showAlertForNotFoundSettings();
        }
    }

    private static void showAlertForNotFoundSettings() {
        VTDConfig config = VTDConfig.getInstance();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(config.getString(StringKey.SETTINGS_CREATOR_SETTINGS_FILE_NOT_FOUND_HEADER));
        alert.setHeaderText(null);
        alert.setContentText(config.getString(StringKey.SETTINGS_CREATOR_SETTINGS_FILE_NOT_FOUND_TEXT));

        alert.showAndWait();
    }
}

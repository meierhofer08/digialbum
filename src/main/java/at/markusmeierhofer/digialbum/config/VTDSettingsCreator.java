package at.markusmeierhofer.digialbum.config;

import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;

public class VTDSettingsCreator {
    private static final Logger LOGGER = LogManager.getLogger();

    // Text strings
    private static final String SETTINGS_FILE_NOT_FOUND_HEADER = "VTDSettings nicht gefunden";
    private static final String SETTINGS_FILE_NOT_FOUND = "Die Datei VTDSettings.properties, welche die "
            + "Programmeinstellungen enthält, wurde nicht im aktuellen Ordner gefunden und wird daher mit den "
            + "Standardwerten neu initialisiert.";

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
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(SETTINGS_FILE_NOT_FOUND_HEADER);
        alert.setHeaderText(null);
        alert.setContentText(SETTINGS_FILE_NOT_FOUND);

        alert.showAndWait();
    }
}

package at.markusmeierhofer.digialbum;

public class VTDConfig {
    private final String dataFilename;
    private final String backupFilename;
    private final String settingsFilename;

    public VTDConfig(String dataFilename, String backupFilename, String settingsFilename) {
        this.dataFilename = dataFilename;
        this.backupFilename = backupFilename;
        this.settingsFilename = settingsFilename;
    }

    public static VTDConfig DEFAULT = new VTDConfig("C:\\VTD\\VTDData.dat",
            "C:\\VTD\\VTDData.txt", "C:\\VTD\\VTDSettings.prop");

    public String getDataFilename() {
        return dataFilename;
    }

    public String getBackupFilename() {
        return backupFilename;
    }

    public String getSettingsFilename() {
        return settingsFilename;
    }
}

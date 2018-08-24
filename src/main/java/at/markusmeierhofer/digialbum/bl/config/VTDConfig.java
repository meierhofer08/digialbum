package at.markusmeierhofer.digialbum.bl.config;

import at.markusmeierhofer.digialbum.bl.config.settings.VTDSettings;

import java.util.ResourceBundle;

public class VTDConfig {
    private static class InstanceHolder {
        private static final VTDConfig INSTANCE = new VTDConfig();
    }

    public static VTDConfig getInstance() {
        return VTDConfig.InstanceHolder.INSTANCE;
    }

    private static final String STRING_BUNDLE_NAME = "strings";

    private VTDSettings settings;
    private ResourceBundle stringBundle;

    private VTDConfig() {
        settings = VTDSettings.getInstance();
        stringBundle = ResourceBundle.getBundle(STRING_BUNDLE_NAME);
    }

    public VTDSettings getSettings() {
        return settings;
    }

    public String getString(StringKey key) {
        return stringBundle.getString(key.getKey());
    }
}

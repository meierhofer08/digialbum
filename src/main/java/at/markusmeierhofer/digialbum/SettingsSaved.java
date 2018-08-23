package at.markusmeierhofer.digialbum;

import java.util.concurrent.atomic.AtomicBoolean;

public class SettingsSaved {
    private static AtomicBoolean settingsSaved = new AtomicBoolean(false);

    public static boolean areSettingsSaved() {
        return settingsSaved.get();
    }

    public static void setSettingsSaved(boolean settingsSaved) {
        SettingsSaved.settingsSaved.set(settingsSaved);
    }
}

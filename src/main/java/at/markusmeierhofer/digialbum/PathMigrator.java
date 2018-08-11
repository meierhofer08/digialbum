package at.markusmeierhofer.digialbum;

import java.util.List;

public class PathMigrator {
    public static void main(String[] args) {
        if (args.length == 0) {
            throw new RuntimeException("Wrong usage! Must supply migration path!");
        }

        String migrationPath = args[0];
        VTDDataAccess dataAccess = VTDDataAccess.getInstance();
        List<VTDEntry> entries = dataAccess.loadData();
        for (VTDEntry entry : entries) {
            if (!entry.getImageUrl().get().contains(migrationPath)) {
                entry.setImageUrl(migrationPath + entry.getImageUrl().get());
            }
        }
        dataAccess.saveData(entries);
    }
}

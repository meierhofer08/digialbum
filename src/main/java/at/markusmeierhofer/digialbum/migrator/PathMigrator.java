package at.markusmeierhofer.digialbum.migrator;

import at.markusmeierhofer.digialbum.dataaccess.DataFileNotFoundException;
import at.markusmeierhofer.digialbum.dataaccess.VTDDataAccess;

import java.util.stream.Collectors;

public class PathMigrator {
    public static void main(String[] args) throws DataFileNotFoundException {
        if (args.length < 2) {
            throw new RuntimeException("Usage: <migration-path> <action>\n" +
                    "Allowed actions: add, remove");
        }

        String migrationPath = args[0];
        MigrationAction action = MigrationAction.from(args[1])
                .orElseThrow(() -> new RuntimeException("Unknown action, only 'add' or 'remove' are allowed!"));
        VTDDataAccess dataAccess = VTDDataAccess.getInstance();
        dataAccess.saveData(dataAccess.loadData().stream()
                .map(entry -> entry.setImageUrl(action.performMigration(migrationPath, entry)))
                .collect(Collectors.toList()));
    }
}

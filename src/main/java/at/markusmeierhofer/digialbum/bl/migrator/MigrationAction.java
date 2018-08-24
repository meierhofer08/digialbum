package at.markusmeierhofer.digialbum.bl.migrator;

import at.markusmeierhofer.digialbum.VTDEntry;

import java.util.Arrays;
import java.util.Optional;

public enum MigrationAction {
    ADD {
        @Override
        public String performMigration(String migrationPath, VTDEntry entry) {
            if (!entry.getImageUrl().get().contains(migrationPath)) {
                return migrationPath + entry.getImageUrl().get();
            } else {
                return entry.getImageUrl().get();
            }
        }
    },
    REMOVE {
        @Override
        public String performMigration(String migrationPath, VTDEntry entry) {
            if (entry.getImageUrl().get().contains(migrationPath)) {
                return entry.getImageUrl().get().replace(migrationPath, "");
            } else {
                return entry.getImageUrl().get();
            }
        }
    };

    public static Optional<MigrationAction> from(String actionName) {
        return Arrays.stream(values())
                .filter(migrationAction -> migrationAction.name().equalsIgnoreCase(actionName))
                .findAny();
    }

    public abstract String performMigration(String migrationPath, VTDEntry entry);
}

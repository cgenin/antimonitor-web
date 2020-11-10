package fr.genin.christophe.antimonitor.dto;

import org.flywaydb.core.api.MigrationInfo;

import java.util.Arrays;
import java.util.List;

public class FlywayInfo {

    public final List<MigrationInfo> appliedMigration;
    public final List<MigrationInfo> currentMigration;
    public final List<MigrationInfo> pendingMigration;

    public FlywayInfo(MigrationInfo[] applied, MigrationInfo current, MigrationInfo[] pending) {
        appliedMigration = Arrays.asList(applied);
        currentMigration = Arrays.asList(current);
        pendingMigration = Arrays.asList(pending);
    }
}

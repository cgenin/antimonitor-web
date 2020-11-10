package fr.genin.christophe.antimonitor.dto;

import org.flywaydb.core.api.MigrationInfoService;

public class Health {

    public final FlywayInfo flyway;
    public final boolean dbState;
    public boolean up = true;

    public Health(MigrationInfoService info, boolean dbState) {
        this.flyway = new FlywayInfo(info.applied(), info.current(), info.pending());
        this.dbState = dbState;
    }
}

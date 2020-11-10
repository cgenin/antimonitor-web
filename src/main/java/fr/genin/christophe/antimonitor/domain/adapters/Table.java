package fr.genin.christophe.antimonitor.domain.adapters;

public class Table {

    public final String id;
    public final String Service;
    public final String tableName;
    public final long lastUpdated;

    public Table(String id, String service, String tableName, long lastUpdated) {
        this.id = id;
        Service = service;
        this.tableName = tableName;
        this.lastUpdated = lastUpdated;
    }
}

package fr.genin.christophe.antimonitor.domain.adapters;

public class Dependency {

    public final String resource;
    public final String usedBy;

    public Dependency(String resource, String usedBy) {
        this.resource = resource;
        this.usedBy = usedBy;
    }
}

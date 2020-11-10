package fr.genin.christophe.antimonitor.domain.adapters;

import io.vertx.core.json.JsonObject;

public class Version {

    public final String id;
    public final JsonObject json;
    public final String name;
    public final String idProject;


    public Version(String id, JsonObject json, String name, String idProject) {
        this.id = id;
        this.json = json;
        this.name = name;
        this.idProject = idProject;
    }

}

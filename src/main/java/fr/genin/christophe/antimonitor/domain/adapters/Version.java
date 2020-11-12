package fr.genin.christophe.antimonitor.domain.adapters;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

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


    public boolean isSnapshot() {
        return json.getBoolean("isSnapshot");

    }

    public String changelog() {
        return json.getString("changelog");
    }

    private List<String> extractListString(String name) {
        return json.getJsonArray(name, new JsonArray()).stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    public List<String> tables() {
        return extractListString("tables");
    }

    public List<String> apis() {
        return extractListString("apis");
    }

    public List<String> javaDeps() {
        return extractListString("javaDeps");

    }

}

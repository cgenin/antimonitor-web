package fr.genin.christophe.antimonitor.domain.adapters;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public class Project {
    public final String id;
    public final String name;
    public final JsonObject document;


    public Project(String id, String name, JsonObject document) {
        this.id = id;
        this.name = name;
        this.document = document;
    }

    public long latestUpdate() {
        return document.getLong("latestUpdate", -1L);
    }



    public Project setSnapshot(String version) {
        document.put("snapshot", version);
        return this;
    }


    public Project setRelease(String version) {
        document.put("release", version);
        return this;
    }


    public Project setTables(List<String> tables) {
        document.put("tables", tables);
        return this;
    }


    public List<String> javaDeps() {
        return document.getJsonArray("javaDeps",new JsonArray())
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }


    public Project setJavaDeps(List<String> javaDeps) {
        document.put("javaDeps", new JsonArray(javaDeps));
        return this;
    }



    public Project setChangeLog(String changeLog) {
        document.put("changelog", changeLog);
        return this;
    }


    public Project setApis(List<String> apis) {
        document.put("apis", apis);
        return this;
    }


    public Project setLatestUpdate(Long update) {
        document.put("latestUpdate", update);
        return this;
    }


    public String id() {
        return document.getString("id");
    }


    public String release() {
        return document.getString("release");
    }


    public String snapshot() {
        return document.getString("snapshot");
    }


    public List<String> tables() {

        return document.getJsonArray("tables", new JsonArray())
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }


    public List<String> apis() {
        return document.getJsonArray("apis", new JsonArray())
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }


    public String changelog() {
        return document.getString("changelog");
    }

}

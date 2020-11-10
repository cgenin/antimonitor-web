package fr.genin.christophe.antimonitor.domain.adapters;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class Api {

    public final String id;
    public final String method;
    public final String fullurl;
    public final String idProject;
    public final JsonObject document;

    public Api(String id, String method, String fullurl, String idProject, JsonObject document) {
        this.id = id;
        this.method = method;
        this.fullurl = fullurl;
        this.idProject = idProject;
        this.document = document;
    }


    public Api setArtifactId(String artifactId) {
        document.put("artifactId", artifactId);
        return this;
    }


    public Api setGroupId(String groupId) {
        document.put("groupId", groupId);

        return this;
    }


    public Api setSince(String version) {
        document.put("since", version);
        return this;
    }


    public Api setLatestUpdate(long update) {
        document.put("latestUpdate", update);
        return this;
    }


    public Api setName(String name) {
        document.put("name", name);

        return this;
    }


    public Api setReturns(String returns) {
        document.put("returns", returns);
        return this;
    }


    public Api setParams(JsonArray params) {
        document.put("params", params);
        return this;
    }


    public Api setComment(String comment) {
        document.put("comment", comment);
        return this;
    }


    public Api setClassName(String className) {
        document.put("className", className);
        return this;
    }
}

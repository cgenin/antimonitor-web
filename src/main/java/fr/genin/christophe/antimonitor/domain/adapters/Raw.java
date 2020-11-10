package fr.genin.christophe.antimonitor.domain.adapters;

import io.vertx.core.json.JsonObject;

public class Raw {

    public final JsonObject json;

    public final Treatments state;

    public final Long update;

    public final Integer id;


    public Raw(JsonObject json, Treatments state, Long update, Integer id) {
        this.json = json;
        this.state = state;
        this.update = update;
        this.id = id;
    }

    public String artifactId() {
        return json.getString("artifactId");
    }


    public String version() {
        return json.getString("version", "");
    }

    public JsonObject apis() {
        return json.getJsonObject("apis", new JsonObject());
    }
}

package fr.genin.christophe.antimonitor.domain.adapters;

import io.vertx.core.json.JsonObject;

import java.time.LocalDateTime;
import java.util.Date;

public class WebApplicationRaw {

    public final String artifactId;
    public final String groupId;
    public final String version;
    public final JsonObject packagesJson;
    public String id;
    public StateCompletion state;
    public LocalDateTime creationDate;


    public WebApplicationRaw(String artifactId, String groupId, String version, JsonObject packagesJson) {
        this.artifactId = artifactId;
        this.groupId = groupId;
        this.version = version;
        this.packagesJson = packagesJson;
    }


}

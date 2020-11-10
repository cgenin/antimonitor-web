package fr.genin.christophe.antimonitor.domain;

import fr.genin.christophe.antimonitor.domain.adapters.Api;
import fr.genin.christophe.antimonitor.domain.adapters.Project;
import fr.genin.christophe.antimonitor.domain.adapters.Raw;
import io.smallrye.mutiny.Multi;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class Apis {

    public Multi<Api> createFrom(Raw raw, Project project) {
        final JsonObject apis = raw.apis();
        final String version = raw.version();
        JsonArray services = apis.getJsonArray("services", new JsonArray());

        return Multi.createFrom().items(services.stream())
                .map(o -> (JsonObject) o)
                .flatMap(obj -> {
                    String className = obj.getString("name", "");
                    JsonArray methods = obj.getJsonArray("methods", new JsonArray());
                    return Multi.createFrom().items(methods.stream())
                            .map(o -> (JsonObject) o)
                            .map(o -> o.put("className", className));
                })
                .map(methodJson -> {
                    String groupId = apis.getString("groupId", "");
                    final String method = methodJson.getString("method", "");
                    final String path = methodJson.getString("path", "");
                    final String id = UUID.randomUUID().toString();
                    final String name = methodJson.getString("name", "");
                    final String returns = methodJson.getString("returns", "");
                    final JsonArray params = methodJson.getJsonArray("params", new JsonArray());
                    final String comment = methodJson.getString("comment", "");
                    final String className = methodJson.getString("className", "");

                    final JsonObject document = new JsonObject().put("id", id);
                    return new Api(id, method, path, project.id, document)
                            .setArtifactId(raw.artifactId())
                            .setGroupId(groupId)
                            .setSince(version)
                            .setLatestUpdate(raw.update)
                            .setName(name)
                            .setReturns(returns)
                            .setParams(params)
                            .setComment(comment)
                            .setClassName(className);
                });
    }
}

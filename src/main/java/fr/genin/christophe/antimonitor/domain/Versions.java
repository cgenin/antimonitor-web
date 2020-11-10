package fr.genin.christophe.antimonitor.domain;

import fr.genin.christophe.antimonitor.domain.adapters.Project;
import fr.genin.christophe.antimonitor.domain.adapters.Raw;
import fr.genin.christophe.antimonitor.domain.adapters.Version;
import io.vertx.core.json.JsonObject;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class Versions {


    public Version updateWith(Version version, Raw raw, Project project) {
        Long lDate = version.json.getLong("latestUpdate", 0L);
        if (lDate > raw.update) {
            return version;
        }


        return new Version(version.id, updateVersion(raw, project), version.name, version.idProject);
    }

    public Version create(Raw raw, Project project) {
        return new Version(UUID.randomUUID().toString(), updateVersion(raw, project), raw.version(), project.id);
    }


    public JsonObject updateVersion(Raw raw, Project project) {

        final String version = raw.version();
        boolean snapshot = DomainUtils.isSnapshot(version);
        List<String> javaDeps = DomainUtils.extractJavaDeps(raw.json);
        List<String> tables = DomainUtils.extractTables(raw.json);
        List<String> urls = DomainUtils.extractUrls(raw.json);
        final JsonObject jsonObject = new JsonObject();
        Optional.ofNullable(raw.json.getString("changelog"))
                .ifPresent(c -> jsonObject.put("changelog", c));
        return jsonObject
                .put("isSnapshot", snapshot)
                .put("javaDeps", javaDeps)
                .put("tables", tables)
                .put("urls", urls);
    }
}

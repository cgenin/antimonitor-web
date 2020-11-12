package fr.genin.christophe.antimonitor.services;

import fr.genin.christophe.antimonitor.DbUtils;
import fr.genin.christophe.antimonitor.domain.DomainUtils;
import fr.genin.christophe.antimonitor.domain.WebApplications;
import fr.genin.christophe.antimonitor.domain.adapters.WebApplicationRaw;
import fr.genin.christophe.antimonitor.domain.output.WebApplicationInsert;
import fr.genin.christophe.antimonitor.dto.Apps;
import fr.genin.christophe.antimonitor.dto.Configuration;
import fr.genin.christophe.antimonitor.jooq.JooqFactory;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.sqlclient.Row;
import org.jooq.JSON;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.genin.christophe.antimonitor.jooq.generated.Tables.WEB_APPLICATION_QUEUE;

@ApplicationScoped
public class WebAppsService implements WebApplicationInsert {

    private final static Logger LOGGER = LoggerFactory.getLogger(WebAppsService.class);
    public static final Function<Row, WebApplicationRaw> ROW_TO_DOMAIN = row -> {
        final String id = row.getString(WEB_APPLICATION_QUEUE.ID.getName());
        final LocalDateTime createdAT = row.getLocalDateTime(WEB_APPLICATION_QUEUE.CREATED_AT.getName());
        final String artifactId = row.getString(WEB_APPLICATION_QUEUE.ARTIFACTID.getName());
        final String groupId = row.getString(WEB_APPLICATION_QUEUE.GROUPID.getName());
        final String version = row.getString(WEB_APPLICATION_QUEUE.VERSION.getName());
        final String pjRaw = row.getValue(WEB_APPLICATION_QUEUE.PACKAGESJSON.getName()).toString();
        final WebApplicationRaw war = new WebApplicationRaw(artifactId, groupId, version, new JsonObject(pjRaw));
        war.id = id;
        war.creationDate = createdAT;
        return war;
    };

    @Inject
    JooqFactory jooqFactory;

    @Inject
    WebApplications webApplications;

    @Inject
    ConfigurationService configurationService;


    public Uni<Boolean> store(Apps apps) {
        return Uni.createFrom().item(apps)
                .map(a -> new WebApplicationRaw(a.artifactId, a.groupId, a.version, new JsonObject(a.packagesJson)))
                .flatMap(wa -> webApplications.insert(wa, this));
    }

    @Override
    public Uni<Boolean> insert(WebApplicationRaw war) {
        return jooqFactory.preparedQuery(dsl -> dsl
                .insertInto(WEB_APPLICATION_QUEUE)
                .columns(WEB_APPLICATION_QUEUE.ID,
                        WEB_APPLICATION_QUEUE.CREATED_AT,
                        WEB_APPLICATION_QUEUE.STATE,
                        WEB_APPLICATION_QUEUE.ARTIFACTID,
                        WEB_APPLICATION_QUEUE.GROUPID,
                        WEB_APPLICATION_QUEUE.VERSION,
                        WEB_APPLICATION_QUEUE.PACKAGESJSON
                )
                .values(
                        war.id,
                        LocalDateTime.now(),
                        war.state.name(),
                        war.artifactId,
                        war.groupId,
                        war.version,
                        JSON.json(war.packagesJson.encode())

                ))
                .onItem()
                .transform(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    public Multi<WebApplicationRaw> findAll() {
        return jooqFactory.preparedQuery(dsl ->
                dsl.select(WEB_APPLICATION_QUEUE.ID,
                        WEB_APPLICATION_QUEUE.CREATED_AT,
                        WEB_APPLICATION_QUEUE.ARTIFACTID,
                        WEB_APPLICATION_QUEUE.GROUPID,
                        WEB_APPLICATION_QUEUE.VERSION,
                        WEB_APPLICATION_QUEUE.PACKAGESJSON)
                        .from(WEB_APPLICATION_QUEUE)
                        .orderBy(WEB_APPLICATION_QUEUE.CREATED_AT)
        )
                .toMulti()
                .flatMap(DbUtils.ROWSET_TO_MULTI_ROW)
                .map(ROW_TO_DOMAIN);

    }

    public Multi<String> findServices() {
        return configurationService.get()
                .map(Configuration::getNpmFilters)
                .onFailure().recoverWithItem(Collections.emptyList())
                .toMulti()
                .flatMap(
                        npmFilters -> findAll()
                                .map(w -> w.packagesJson)
                                .map(p -> p.getJsonObject("dependencies", new JsonObject()))
                                .flatMap(dependencies -> {
                                    final Stream<String> distinct = dependencies.fieldNames().stream()
                                            .filter(key -> npmFilters.stream().anyMatch(key::contains))
                                            .map(key -> npmFilters.stream().filter(key::contains)
                                                    .reduce(key, (tmp, k) -> tmp.replaceAll(k, "")))
                                            .distinct();
                                    return Multi.createFrom().items(distinct);
                                })

                );
    }

    public Multi<JsonObject> groupBy() {
        return configurationService.get()
                .map(Configuration::getNpmFilters)
                .onFailure().recoverWithItem(Collections.emptyList())
                .toMulti()
                .flatMap(
                        npmFilters ->
                                findAll()
                                        .groupItems().by(war -> war.groupId + "." + war.artifactId)
                                        .flatMap(grouped -> grouped.collectItems().in(() -> new JsonObject()
                                                        .put("lastUpdateSnapshot", 0L)
                                                        .put("lastUpdateRelease", 0L)
                                                        .put("packageJson", new JsonObject())
                                                        .put("lastUpdate", 0L),
                                                (acc, fa) -> {
                                                    acc.put("groupId", fa.groupId)
                                                            .put("artifactId", fa.artifactId);
                                                    final long creationDate = fa.creationDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                                                    if (DomainUtils.isSnapshot(fa.version)) {
                                                        if (acc.getLong("lastUpdateSnapshot") < creationDate) {
                                                            acc.put("snapshotVersion", fa.version)
                                                                    .put("lastUpdateSnapshot", creationDate);
                                                        }
                                                    } else if (acc.getLong("lastUpdateRelease") < creationDate) {
                                                        acc.put("releaseVersion", fa.version)
                                                                .put("lastUpdateRelease", creationDate);
                                                    }

                                                    if (acc.getLong("lastUpdate") < creationDate) {
                                                        JsonObject packagesJson = fa.packagesJson;
                                                        acc.put("packageJson", packagesJson)
                                                                .put("lastUpdate", creationDate);
                                                        acc.put("servicesClient", servicesClient(packagesJson, npmFilters));
                                                    }
                                                }).toMulti()));
    }

    private JsonArray servicesClient(JsonObject packagesJson, List<String> npmFilters) {
        List<JsonObject> collect = serviceClientList(packagesJson, npmFilters);
        return new JsonArray(collect);
    }

    private List<JsonObject> serviceClientList(JsonObject packagesJson, List<String> npmFilters) {
        HashSet<String> npms = new HashSet<>(npmFilters);
        JsonObject dependencies = packagesJson.getJsonObject("dependencies", new JsonObject()).copy();
        return dependencies.fieldNames().stream()
                .filter(key -> npms.stream().anyMatch(key::contains))
                .distinct()
                .map(key -> new JsonObject().put("key", key).put("value", dependencies.getString(key)))
                .collect(Collectors.toList());
    }

    public Multi<WebApplicationRaw> selectLastForGroupIdArtifactId() {
        return jooqFactory.preparedQuery(
                dsl -> dsl.select(WEB_APPLICATION_QUEUE.ID,
                        WEB_APPLICATION_QUEUE.CREATED_AT,
                        WEB_APPLICATION_QUEUE.ARTIFACTID,
                        WEB_APPLICATION_QUEUE.GROUPID,
                        WEB_APPLICATION_QUEUE.VERSION,
                        WEB_APPLICATION_QUEUE.PACKAGESJSON)
                        .from(WEB_APPLICATION_QUEUE)
                        .where(DSL.row(WEB_APPLICATION_QUEUE.CREATED_AT,
                                WEB_APPLICATION_QUEUE.ARTIFACTID,
                                WEB_APPLICATION_QUEUE.GROUPID)
                                .in(
                                        dsl.select(DSL.max(WEB_APPLICATION_QUEUE.CREATED_AT),
                                                WEB_APPLICATION_QUEUE.ARTIFACTID,
                                                WEB_APPLICATION_QUEUE.GROUPID)
                                                .from(WEB_APPLICATION_QUEUE)
                                                .groupBy(WEB_APPLICATION_QUEUE.ARTIFACTID,
                                                        WEB_APPLICATION_QUEUE.GROUPID)
                                )
                        )
        )
                .toMulti()
                .flatMap(DbUtils.ROWSET_TO_MULTI_ROW)
                .map(ROW_TO_DOMAIN);
    }

    public Multi<JsonObject> findByDomain(String domain) {
        Function<WebApplicationRaw, List<String>> extract = fa -> {
            JsonObject dependencies = fa.packagesJson
                    .getJsonObject("dependencies", new JsonObject());
            return dependencies
                    .fieldNames()
                    .stream()
                    .map(String::toUpperCase)
                    .filter(key -> key.contains(domain))
                    .collect(Collectors.toList());
        };

        return configurationService.get()
                .map(Configuration::getNpmFilters)
                .onFailure().recoverWithItem(Collections.emptyList())
                .toMulti()
                .flatMap(
                        npmFilters -> selectLastForGroupIdArtifactId()
                                .filter(fa -> !extract.apply(fa).isEmpty())
                                .map((fa) -> {
                                            final JsonObject acc = new JsonObject();
                                            acc.put("groupId", fa.groupId);
                                            acc.put("artifactId", fa.artifactId);
                                            final long creationDate = fa.creationDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                                            acc.put("lastUpdate", creationDate);
                                            JsonObject dependencies = fa.packagesJson
                                                    .getJsonObject("dependencies", new JsonObject());
                                            final List<String> servicesNames = extract.apply(fa);
                                            List<JsonObject> list = servicesNames.parallelStream()
                                                    .map(key -> new JsonObject().put("name", key).put("version", dependencies.getString(key)))
                                                    .collect(Collectors.toList());
                                            acc.put("services", new JsonArray(list));
                                            return acc;
                                        }
                                )
                );
    }
}

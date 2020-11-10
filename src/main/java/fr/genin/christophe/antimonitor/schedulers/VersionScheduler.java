package fr.genin.christophe.antimonitor.schedulers;

import fr.genin.christophe.antimonitor.domain.Versions;
import fr.genin.christophe.antimonitor.domain.adapters.Project;
import fr.genin.christophe.antimonitor.domain.adapters.Raw;
import fr.genin.christophe.antimonitor.domain.adapters.Treatments;
import fr.genin.christophe.antimonitor.domain.adapters.Version;
import fr.genin.christophe.antimonitor.domain.adapters.exception.EmptyQueueException;
import fr.genin.christophe.antimonitor.jooq.JooqFactory;
import fr.genin.christophe.antimonitor.services.ProjectService;
import fr.genin.christophe.antimonitor.services.RawService;
import fr.genin.christophe.antimonitor.services.VersionService;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jooq.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.function.Function;

import static fr.genin.christophe.antimonitor.jooq.generated.Tables.VERSIONS;

@ApplicationScoped
public class VersionScheduler {


    private static final Logger LOGGER = LoggerFactory.getLogger(VersionScheduler.class);
    public static final Function<RowSet<Row>, Boolean> IS_CREATED_OR_DELETED = rowset -> rowset.rowCount() > 0;

    @Inject
    RawService rawService;

    @Inject
    JooqFactory jooqFactory;

    @Inject
    VersionService versionService;

    @Inject
    ProjectService projectService;

    @Inject
    Versions versions;


    @Scheduled(every = "{fgca.version.scheduler}")
    public void run() {
        rawService.getFirstWithState(Treatments.VERSION)
                .flatMap(raw -> projectService.findByArtifact(raw.artifactId())
                        .map(project -> Tuple.of(raw, project)))
                .flatMap(tuple -> {
                    final Raw raw = tuple.get(Raw.class, 0);

                    final Project project = tuple.get(Project.class, 1);
                    return versionService.findByIdProjectAndVersion(project.id, raw.version())
                            .flatMap(list -> {
                                if (list.isEmpty()) {
                                    return createAndInsert(raw, project);
                                }
                                final Version oldVersion = list.get(0);
                                return update(raw, project, oldVersion);
                            });
                })
                .flatMap(updateRawState())
                .subscribe()
                .with(
                        id -> LOGGER.debug("Changed state  to version for " + id),
                        ex -> {
                            if (ex instanceof EmptyQueueException) {
                                return;
                            }
                            LOGGER.error("An Error occured ", ex);
                        }
                );

    }

    private Uni<Integer> update(Raw raw, Project project, Version oldVersion) {
        return Uni.createFrom().item(oldVersion)
                .map(version -> versions.updateWith(version, raw, project))
                .flatMap(v -> jooqFactory.preparedQuery(dsl ->
                                dsl.update(VERSIONS)
                                        .set(VERSIONS.DOCUMENT, JSON.json(v.json.encode()))
                                        .where(VERSIONS.ID.eq(v.id))
                        )
                )
                .map(rowset -> {
                    if (rowset.rowCount() == 0) {
                        LOGGER.error("No update in version for " + raw.id);
                    }
                    return raw.id;
                });
    }

    private Uni<Integer> createAndInsert(Raw raw, Project project) {
        return Uni.createFrom().item(versions.create(raw, project))
                .flatMap(v -> jooqFactory.preparedQuery(dsl ->
                        dsl.insertInto(VERSIONS)
                                .columns(VERSIONS.ID, VERSIONS.IDPROJECT, VERSIONS.NAME, VERSIONS.DOCUMENT)
                                .values(v.id, v.idProject, v.name, JSON.json(v.json.encode()))
                ))
                .map(rowset -> {
                    if (rowset.rowCount() == 0) {
                        LOGGER.error("No insert in version for " + raw.id);
                    }
                    return raw.id;
                });
    }

    private Function<Integer, Uni<? extends Integer>> updateRawState() {
        return id -> rawService.setTreatementForId(id, Treatments.URL)
                .map(b -> id);
    }
}

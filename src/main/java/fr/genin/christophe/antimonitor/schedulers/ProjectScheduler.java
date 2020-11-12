package fr.genin.christophe.antimonitor.schedulers;

import fr.genin.christophe.antimonitor.jooq.JooqFactory;
import fr.genin.christophe.antimonitor.jooq.JooqWrapper;
import fr.genin.christophe.antimonitor.domain.Projects;
import fr.genin.christophe.antimonitor.domain.adapters.Project;
import fr.genin.christophe.antimonitor.domain.adapters.Raw;
import fr.genin.christophe.antimonitor.domain.adapters.Treatments;
import fr.genin.christophe.antimonitor.domain.adapters.exception.EmptyQueueException;
import fr.genin.christophe.antimonitor.domain.adapters.exception.NoUpdateRequireException;
import fr.genin.christophe.antimonitor.services.ConfigurationService;
import fr.genin.christophe.antimonitor.services.ProjectService;
import fr.genin.christophe.antimonitor.services.RawService;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import org.jooq.JSON;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

import static fr.genin.christophe.antimonitor.jooq.generated.Tables.PROJECTS;

@ApplicationScoped
public class ProjectScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectScheduler.class);

    @Inject
    RawService rawService;

    @Inject
    ConfigurationService configurationService;

    @Inject
    JooqFactory jooqFactory;

    @Inject
    ProjectService projectService;

    @Inject
    Projects projects;


    @Inject
    PgPool client;

    @Scheduled(every = "{fgca.project.scheduler}")
    public void treatProjects() {
        // Get All params
        Uni.combine().all().unis(
                rawService.getFirstWithState(Treatments.PROJECTS),
                projectService.findAll(),
                configurationService.get()
        )
                .asTuple()
                .flatMap(tuple -> {
                    Raw raw = tuple.getItem1();
                    // Call domain
                    return projects.createOrUpdate(raw, tuple.getItem2(), tuple.getItem3())
                            .map(createSaveQuery())
                            .flatMap(executeQuery())
                            .map(onSuccessQuery(raw))
                            .onFailure()
                            .recoverWithItem(logOnFail(raw));
                })
                .flatMap(id -> rawService.setTreatementForId(id, Treatments.TABLES))
                .subscribe()
                .with(
                        id -> LOGGER.info("Changed state  to version for " + id),
                        ex -> {
                            if (ex instanceof EmptyQueueException) {
                                return;
                            }
                            LOGGER.error("An Error occured ", ex);
                        }
                );

    }

    private Function<JooqWrapper, Uni<? extends RowSet<Row>>> executeQuery() {
        return sql -> client.preparedQuery(sql.getSQL()).execute(sql.getParams());
    }

    private Function<RowSet<Row>, Integer> onSuccessQuery(Raw raw) {
        return r -> {
            final int id = raw.id;

            if (r.rowCount() > 0) {
                LOGGER.info("Insert Or Update for" + id);
            }
            return id;
        };
    }

    private Function<Throwable, Integer> logOnFail(Raw raw) {
        return ex -> {
            final int id = raw.id;
            if (ex instanceof NoUpdateRequireException) {
                LOGGER.debug("" + id, ex);
            } else {
                LOGGER.error("Error occured for Project " + id, ex);
            }

            return id;
        };
    }


    private Function<Project, JooqWrapper> createSaveQuery() {
        return p -> {
            if (Objects.nonNull(p.id)) {
                return jooqFactory.query(dsl ->
                        dsl.update(PROJECTS)
                                .set(PROJECTS.ID, p.id)
                                .set(PROJECTS.NAME, p.name)
                                .set(PROJECTS.DOCUMENT, JSON.json(p.document.encode()))
                );
            }
            return jooqFactory.query(dsl ->
                    dsl.insertInto(PROJECTS)
                            .columns(PROJECTS.ID, PROJECTS.NAME, PROJECTS.DOCUMENT)
                            .values(UUID.randomUUID().toString(), p.name, JSON.json(p.document.encode()))
            );
        };
    }


}

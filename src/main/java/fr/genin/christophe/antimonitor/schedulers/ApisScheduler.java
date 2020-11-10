package fr.genin.christophe.antimonitor.schedulers;

import fr.genin.christophe.antimonitor.domain.Apis;
import fr.genin.christophe.antimonitor.domain.adapters.Api;
import fr.genin.christophe.antimonitor.domain.adapters.Project;
import fr.genin.christophe.antimonitor.domain.adapters.Raw;
import fr.genin.christophe.antimonitor.domain.adapters.Treatments;
import fr.genin.christophe.antimonitor.domain.adapters.exception.EmptyQueueException;
import fr.genin.christophe.antimonitor.jooq.JooqFactory;
import fr.genin.christophe.antimonitor.services.ApisService;
import fr.genin.christophe.antimonitor.services.ProjectService;
import fr.genin.christophe.antimonitor.services.RawService;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jooq.JSON;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.function.Function;

import static fr.genin.christophe.antimonitor.jooq.generated.Tables.APIS;

@ApplicationScoped
public class ApisScheduler {


    private static final Logger LOGGER = LoggerFactory.getLogger(ApisScheduler.class);

    @Inject
    RawService rawService;

    @Inject
    JooqFactory jooqFactory;

    @Inject
    ApisService apisService;

    @Inject
    ProjectService projectService;

    @Inject
    Apis apis;


    @Scheduled(every = "{fgca.apis.scheduler}")
    public void run() {
        rawService.getFirstWithState(Treatments.URL)
                .flatMap(raw -> projectService.findByArtifact(raw.artifactId())
                        .map(project -> Tuple.of(raw, project)))
                .flatMap(t -> {
                    final Raw raw = t.get(Raw.class, 0);
                    final Project project = t.get(Project.class, 1);
                    return apisService.deletedByIdProject(project.id)
                            .flatMap(nb -> {
                                LOGGER.info("Deleted apis " + nb + " for " + raw.id);
                                return apis.createFrom(raw, project)
                                        .flatMap(executeInsert())
                                        .map(rowset -> rowset.rowCount() > 0)
                                        .collectItems().asList();
                            })
                            .map(list -> {
                                final long count = list.stream().filter(b -> b).count();
                                if (count !=  list.size()) {
                                    LOGGER.error(" error in insertion "+ count + " / "+ list.size() + " for "+raw.id);
                                }
                                return raw.id;
                            });
                })
                .flatMap(updateRawState())
                .subscribe()
                .with(
                        id -> LOGGER.info("Changed state  to apis for " + id),
                        ex -> {
                            if (ex instanceof EmptyQueueException) {
                                return;
                            }
                            LOGGER.error("An Error occured ", ex);
                        }
                );

    }

    private Function<Api, Publisher<? extends RowSet<Row>>> executeInsert() {
        return api -> jooqFactory.preparedQuery(
                dsl -> dsl.insertInto(APIS)
                        .columns(APIS.ID, APIS.IDPROJECT, APIS.METHOD, APIS.FULLURL, APIS.DOCUMENT)
                        .values(api.id, api.idProject, api.method, api.fullurl,
                                JSON.json(api.document.encode()))
        ).toMulti();
    }

    private Function<Integer, Uni<? extends Integer>> updateRawState() {
        return id -> rawService.setTreatementForId(id, Treatments.DEPENDENCIES)
                .map(b -> id);
    }
}

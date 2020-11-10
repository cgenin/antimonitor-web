package fr.genin.christophe.antimonitor.schedulers;

import fr.genin.christophe.antimonitor.domain.Dependencies;
import fr.genin.christophe.antimonitor.domain.adapters.Treatments;
import fr.genin.christophe.antimonitor.domain.adapters.exception.EmptyQueueException;
import fr.genin.christophe.antimonitor.jooq.JooqFactory;
import fr.genin.christophe.antimonitor.services.ConfigurationService;
import fr.genin.christophe.antimonitor.services.DependenciesService;
import fr.genin.christophe.antimonitor.services.ProjectService;
import fr.genin.christophe.antimonitor.services.RawService;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.function.Function;

import static fr.genin.christophe.antimonitor.jooq.generated.Tables.DEPENDENCIES;

@ApplicationScoped
public class DependenciesScheduler {


    private static final Logger LOGGER = LoggerFactory.getLogger(DependenciesScheduler.class);

    @Inject
    RawService rawService;

    @Inject
    JooqFactory jooqFactory;


    @Inject
    ConfigurationService configurationService;

    @Inject
    DependenciesService dependenciesService;

    @Inject
    ProjectService projectService;

    @Inject
    Dependencies dependencies;


    @Scheduled(every = "{fgca.dependencies.scheduler}")
    public void run() {

        // Get All params
        rawService.getFirstWithState(Treatments.DEPENDENCIES)
                .flatMap(raw -> {
                    String usedBy = Dependencies.format(raw.artifactId());
                    return dependenciesService.deleteByUsedBy(usedBy)
                            .flatMap(b -> projectService.findByArtifact(raw.artifactId()))
                            .toMulti()
                            .flatMap(project -> dependencies.createFrom(raw, project))
                            .flatMap(
                                    dependency ->
                                            jooqFactory.preparedQuery(dsl -> dsl.insertInto(DEPENDENCIES)
                                                    .columns(DEPENDENCIES.RESOURCE, DEPENDENCIES.USED_BY)
                                                    .values(dependency.resource, dependency.usedBy)

                                            )
                                                    .toMulti()
                            )
                            .map(rows -> rows.rowCount() > 0)
                            .collectItems().asList()
                            .map(list -> {
                                final long c = list.stream().filter(b -> b).count();
                                LOGGER.info("created " + c + " on " + list.size() + " for " + raw.id);
                                return raw.id;
                            });

                })
                .flatMap(updateRawState())
                .subscribe()
                .with(
                        id -> LOGGER.info("Changed state  to dependencies for " + id),
                        ex -> {
                            if (ex instanceof EmptyQueueException) {
                                return;
                            }
                            LOGGER.error("An Error occured ", ex);
                        }
                );

    }


    private Function<Integer, Uni<? extends Integer>> updateRawState() {
        return id -> rawService.setTreatementForId(id, Treatments.END)
                .map(b -> id);
    }
}

package fr.genin.christophe.antimonitor.services;

import fr.genin.christophe.antimonitor.domain.adapters.Treatments;
import fr.genin.christophe.antimonitor.jooq.JooqFactory;
import fr.genin.christophe.antimonitor.jooq.generated.Tables;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.jooq.JSON;
import org.jooq.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Date;

import static fr.genin.christophe.antimonitor.jooq.generated.Tables.QUEUE;

@ApplicationScoped
public class AppsService {

    private final static Logger LOGGER = LoggerFactory.getLogger(AppsService.class);


    @Inject
    JooqFactory jooqFactory;
    @Inject
    RawService rawService;

    public Uni<JsonObject> insert(JsonObject input) {

        return jooqFactory.preparedQuery(dsl -> dsl.insertInto(QUEUE)
                .columns(QUEUE.DOCUMENT, QUEUE.STATE, QUEUE.LATEST_UPDATE)
                .values(JSON.json(input.encode()), Treatments.PROJECTS.getState(), new Date().getTime())
        )
                .map(r -> input);
    }

    public Uni<JsonObject> deleteAll() {

        return deleteAllCalculate()
                .flatMap(c -> rawService.setTreatements(Treatments.PROJECTS))
                .map(nb -> new JsonObject().put("queued", nb));
    }

    private Uni<Boolean> deleteFrom(Table<?> table) {
        return jooqFactory.preparedQuery(dsl -> dsl.delete(table))
                .onItem().transform(rows -> {
                    final int count = rows.rowCount();
                    LOGGER.info("Delete " + count + " in " + table.getName());
                    return true;
                });
    }

    public Uni<JsonObject> deleteAllCalculate() {
        return
                deleteFrom(Tables.APIS)
                        .chain(() -> deleteFrom(Tables.DEPENDENCIES))
                        .chain(() -> deleteFrom(Tables.PROJECTS))
                        .chain(() -> deleteFrom(Tables.TABLES))
                        .chain(() -> deleteFrom(Tables.VERSIONS))
                        .onItem().transform(b -> new JsonObject());
    }
}

package fr.genin.christophe.antimonitor.services;

import fr.genin.christophe.antimonitor.DbUtils;
import fr.genin.christophe.antimonitor.domain.adapters.Raw;
import fr.genin.christophe.antimonitor.domain.adapters.Treatments;
import fr.genin.christophe.antimonitor.domain.adapters.exception.EmptyQueueException;
import fr.genin.christophe.antimonitor.jooq.JooqFactory;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowIterator;
import io.vertx.mutiny.sqlclient.SqlResult;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Objects;

import static fr.genin.christophe.antimonitor.jooq.generated.Tables.QUEUE;

@ApplicationScoped
public class RawService {


    @Inject
    JooqFactory jooqFactory;

    public Uni<Raw> getFirstWithState(Treatments treatments) {
        return jooqFactory.preparedQuery(dsl ->
                dsl.select(QUEUE.ID, QUEUE.DOCUMENT, QUEUE.STATE, QUEUE.LATEST_UPDATE)
                        .from(QUEUE)
                        .where(QUEUE.STATE.equal(treatments.getState()))
                        .orderBy(QUEUE.LATEST_UPDATE).limit(1)
        )
                .flatMap(rows -> {
                    final RowIterator<Row> iterator = rows.iterator();
                    if (iterator.hasNext()) {
                        return Uni.createFrom().item(iterator.next());
                    }
                    return Uni.createFrom().failure(new EmptyQueueException());
                })
                .map(row -> {
                    final String jsonStr = row.getString(QUEUE.DOCUMENT.getName());
                    final Treatments state = Treatments.parse(row.getInteger(QUEUE.STATE.getName()));
                    final Long lastUpdate = row.getLong(QUEUE.LATEST_UPDATE.getName());
                    final Integer id = row.getInteger(QUEUE.ID.getName());
                    return new Raw(new JsonObject(jsonStr), state, lastUpdate, id);
                });
    }

    public Uni<Integer> setTreatements(Treatments treatments) {
        return jooqFactory.preparedQuery(dsl -> dsl.update(QUEUE)
                .set(QUEUE.STATE, treatments.getState())
        ).map(SqlResult::rowCount);
    }

    public Uni<Integer> setTreatementForId(Integer id, Treatments treatments) {
        return jooqFactory.preparedQuery(dsl -> dsl.update(QUEUE)
                .set(QUEUE.STATE, treatments.getState())
                .where(QUEUE.ID.eq(id))
        )
                .map(b -> id);
    }

    public Multi<JsonObject> findAllOrderbyTime(Integer baselimit, Integer baseoffset) {
        int limit = (Objects.nonNull(baselimit)) ? baselimit : 10;
        int offset = (Objects.nonNull(baseoffset)) ? baseoffset : 0;


        return jooqFactory.preparedQuery(dsl -> dsl.select(QUEUE.DOCUMENT)
                .from(QUEUE)
                .orderBy(QUEUE.LATEST_UPDATE)
                .limit(limit)
                .offset(offset)
        )
                .toMulti()
                .flatMap(DbUtils.ROWSET_TO_MULTI_ROW)
                .map(row -> row.getString(QUEUE.DOCUMENT.getName()))
                .map(JsonObject::new);

    }
}

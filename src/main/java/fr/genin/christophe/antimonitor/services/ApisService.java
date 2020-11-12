package fr.genin.christophe.antimonitor.services;

import fr.genin.christophe.antimonitor.jooq.JooqFactory;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.SqlResult;
import org.reactivestreams.Publisher;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.StreamSupport;

import static fr.genin.christophe.antimonitor.jooq.generated.Tables.APIS;

@ApplicationScoped
public class ApisService {

    public static final Function<RowSet<Row>, Publisher<? extends JsonObject>> ROW_JSONOBJECT = rowSet -> Multi.createFrom().items(StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(rowSet.iterator(), Spliterator.ORDERED),
            false))
            .map(row -> {
                final String path = row.getString(APIS.FULLURL.getName());
                final String method = row.getString(APIS.METHOD.getName());
                return new JsonObject(row.getString(APIS.DOCUMENT.getName()))
                        .put("path", path)
                        .put("method", method)
                        ;
            });
    @Inject
    JooqFactory jooqFactory;

    public Multi<JsonObject> findAll() {
        return jooqFactory.preparedQuery(
                dsl -> dsl.select(APIS.FULLURL, APIS.METHOD, APIS.DOCUMENT)
                        .from(APIS)
                        .orderBy(APIS.FULLURL, APIS.METHOD, APIS.IDPROJECT)
        )
                .toMulti()
                .flatMap(ROW_JSONOBJECT);
    }

    public Multi<JsonObject> findPaginated(Integer queryLimit, Integer queryOffset) {
        int limit = (Objects.nonNull(queryLimit)) ? queryLimit : 25;
        int offset = (Objects.nonNull(queryOffset)) ? queryOffset : 0;

        return jooqFactory.preparedQuery(
                dsl -> dsl.select(APIS.FULLURL, APIS.METHOD, APIS.DOCUMENT)
                        .from(APIS)
                        .orderBy(APIS.FULLURL, APIS.METHOD, APIS.IDPROJECT)
                        .limit(limit)
                        .offset(offset)
        )
                .toMulti()
                .flatMap(ROW_JSONOBJECT);
    }


    public Uni<Integer> deletedByIdProject(String idProject) {
        return jooqFactory.preparedQuery(
                dsl -> dsl.deleteFrom(APIS).where(APIS.IDPROJECT.eq(idProject))
        )
                .map(SqlResult::rowCount);
    }

}

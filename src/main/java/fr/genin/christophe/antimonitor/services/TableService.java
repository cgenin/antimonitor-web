package fr.genin.christophe.antimonitor.services;

import fr.genin.christophe.antimonitor.domain.adapters.Table;
import fr.genin.christophe.antimonitor.jooq.JooqFactory;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static fr.genin.christophe.antimonitor.jooq.generated.Tables.TABLES;
import static org.jooq.impl.DSL.count;

@ApplicationScoped
public class TableService {

    public static final Function<RowSet<Row>, List<Table>> ROWSET_TO_DOMAINE = rowSet ->
            StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(rowSet.iterator(), Spliterator.ORDERED),
                    false)
                    .map(row -> {
                        final String id = row.getString(TABLES.ID.getName());
                        final String name = row.getString(TABLES.NAME.getName());
                        final String service = row.getString(TABLES.SERVICE.getName());
                        final Long update = row.getLong(TABLES.LATESTUPDATE.getName());
                        return new Table(id, service, name, update);
                    })
                    .collect(Collectors.toList());
    @Inject
    JooqFactory jooqFactory;

    private static final Function<Row, HashMap<String, Long>> FUNC = a -> {
        final String name = a.getString(TABLES.SERVICE.getName());
        final Long nb = a.getLong("nb");
        final HashMap<String, Long> map = new HashMap<>();
        map.put(name, nb);
        return map;
    };

    public Uni<List<Table>> findAll() {
        return jooqFactory.preparedQuery(dsl -> dsl.select(TABLES.ID, TABLES.NAME, TABLES.SERVICE, TABLES.LATESTUPDATE)
                .from(TABLES)
                .orderBy(TABLES.LATESTUPDATE)
        )
                .onItem().transform(ROWSET_TO_DOMAINE);
    }

    public Uni<Map<String, Long>> countTableByProject() {
        return jooqFactory.preparedQuery(dsl -> dsl.select(TABLES.SERVICE, count().as("nb"))
                .from(TABLES)
                .groupBy(TABLES.SERVICE)
        )
                .map(rowSet -> StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(rowSet.iterator(), Spliterator.ORDERED),
                        false)
                        .reduce(new HashMap<>(), (acc, b) -> {
                            acc.putAll(FUNC.apply(b));
                            return acc;
                        }, (a, b) -> {
                            a.putAll(b);
                            return a;
                        })
                );
    }
}

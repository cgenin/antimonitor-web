package fr.genin.christophe.antimonitor.schedulers;

import fr.genin.christophe.antimonitor.domain.Tables;
import fr.genin.christophe.antimonitor.domain.adapters.Raw;
import fr.genin.christophe.antimonitor.domain.adapters.Table;
import fr.genin.christophe.antimonitor.domain.adapters.Treatments;
import fr.genin.christophe.antimonitor.domain.adapters.exception.EmptyQueueException;
import fr.genin.christophe.antimonitor.jooq.JooqFactory;
import fr.genin.christophe.antimonitor.services.RawService;
import fr.genin.christophe.antimonitor.services.TableService;
import io.quarkus.scheduler.Scheduled;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.function.Function;

import static fr.genin.christophe.antimonitor.jooq.generated.Tables.TABLES;

@ApplicationScoped
public class TableScheduler {


    private static final Logger LOGGER = LoggerFactory.getLogger(TableScheduler.class);
    public static final Function<RowSet<Row>, Boolean> IS_CREATED_OR_DELETED = rowset -> rowset.rowCount() > 0;

    @Inject
    RawService rawService;

    @Inject
    JooqFactory jooqFactory;

    @Inject
    TableService tableService;

    @Inject
    Tables tables;


    @Scheduled(every = "{fgca.table.scheduler}")
    public void run() {

        Uni.combine().all().unis(
                rawService.getFirstWithState(Treatments.TABLES),
                tableService.findAll()
        )
                .asTuple()
                .flatMap(tuple -> {
                    final Raw raw = tuple.getItem1();
                    final List<Table> allTables = tuple.getItem2();
                    return Uni.combine().all().unis(
                            createNewEntry(raw, allTables),
                            deleteEntries(raw, allTables)
                    )
                            .asTuple()
                            .flatMap(updateRawState(raw));
                })

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

    private Function<Tuple2<Integer, Integer>, Uni<? extends Integer>> updateRawState(Raw raw) {
        return tuple2 -> {
            LOGGER.info("Created tables " + tuple2.getItem1() + " / deleted tables " + tuple2.getItem2() + " for " + raw.id);
            return rawService.setTreatementForId(raw.id, Treatments.VERSION)
                    .map(b -> raw.id);
        };
    }

    private Uni<Integer> deleteEntries(Raw raw, List<Table> allTables) {
        return tables.deletionId(raw, allTables)
                .flatMap(id -> jooqFactory.preparedQuery(dsl ->
                                dsl.deleteFrom(TABLES)
                                        .where(TABLES.ID.eq(id))
                        )
                                .map(IS_CREATED_OR_DELETED)
                                .toMulti()
                ).collectItems().asList()
                .map(list -> {
                    if (list.isEmpty()) {
                        return 0;
                    }
                    final long c = list.stream().filter(b -> b).count();
                    if (c != list.size()) {
                        LOGGER.error("Deleted " + c + " on " + list.size() + " for " + raw.id);
                    }
                    return list.size();
                });
    }

    private Uni<Integer> createNewEntry(Raw raw, List<Table> allTables) {
        return tables.getTableToCreate(raw, allTables)
                .flatMap(table -> jooqFactory.preparedQuery(dsl ->
                                dsl.insertInto(TABLES)
                                        .columns(TABLES.ID, TABLES.NAME, TABLES.SERVICE, TABLES.LATESTUPDATE)
                                        .values(table.id, table.tableName, table.Service, table.lastUpdated)
                        )
                                .map(IS_CREATED_OR_DELETED)
                                .toMulti()
                ).collectItems().asList()
                .map(list -> {
                    if (list.isEmpty()) {
                        return 0;
                    }
                    final long c = list.stream().filter(b -> b).count();
                    if (c != list.size()) {
                        LOGGER.error("Created " + c + " on " + list.size() + " for " + raw.id);
                    }
                    return list.size();
                });
    }

}

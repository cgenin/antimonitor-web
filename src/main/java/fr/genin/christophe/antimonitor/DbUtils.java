package fr.genin.christophe.antimonitor;

import io.smallrye.mutiny.Multi;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;

import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class DbUtils {

    public final static Function<RowSet<Row>, Stream<Row>> ROWSET_TO_STREAM_ROW = rowset -> StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(rowset.iterator(), Spliterator.ORDERED),
            false);
    public final static Function<RowSet<Row>, Multi<Row>> ROWSET_TO_MULTI_ROW = rowset -> Multi
            .createFrom().items(ROWSET_TO_STREAM_ROW.apply(rowset));

    private DbUtils() {
    }

    public final static <T> Function<RowSet<Row>, List<T>> rowsetTo(Function<Row, T> transform) {
        return rowSet ->
                StreamSupport.stream(
                        Spliterators.spliteratorUnknownSize(rowSet.iterator(), Spliterator.ORDERED),
                        false)
                        .map(transform)
                        .collect(Collectors.toList());
    }

}

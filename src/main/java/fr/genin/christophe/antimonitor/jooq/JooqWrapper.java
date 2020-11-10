package fr.genin.christophe.antimonitor.jooq;

import io.vertx.mutiny.sqlclient.Tuple;
import org.jooq.JSON;
import org.jooq.Query;
import org.jooq.conf.ParamType;

import java.util.regex.Pattern;

public class JooqWrapper {
    /**
     * Replace ':' but not '::'
     */
    private static final Pattern pattern = Pattern.compile("(?<!:):(?!:)");
    private final Query query;


     JooqWrapper(Query query) {
        this.query = query;

    }

    public String getSQL() {

        final String sql = query
                .getSQL(ParamType.NAMED);
        return pattern.matcher(sql).replaceAll("\\$");
    }

    public Tuple getParams() {
        final Tuple tuple = Tuple.tuple();
        query.getBindValues().forEach(obj -> {
            if (obj instanceof JSON) {
                JSON json = (JSON) obj;
                tuple.addString(json.data());
                return;
            }
            tuple.addValue(obj);
        });
        return tuple;
    }
}

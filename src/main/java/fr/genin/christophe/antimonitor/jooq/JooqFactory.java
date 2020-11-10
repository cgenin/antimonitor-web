package fr.genin.christophe.antimonitor.jooq;

import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.function.Function;
import java.util.regex.Pattern;

@ApplicationScoped
public class JooqFactory {

    @Inject
    PgPool client;

    private DSLContext dsl;


    @PostConstruct
    public void initialize(){
        this.dsl = DSL.using(SQLDialect.POSTGRES);
    }


    public JooqWrapper query(Function<DSLContext, Query> func) {
        final Query q = func.apply(dsl);
        return new JooqWrapper(q);
    }

    public Uni<RowSet<Row>> preparedQuery(Function<DSLContext, Query> func) {
        final JooqWrapper jooqWrapper = query(func);
        final String sql = jooqWrapper.getSQL();
        final Tuple params = jooqWrapper.getParams();
        return client.preparedQuery(sql).execute(params);
    }


}

package fr.genin.christophe.antimonitor.web;

import fr.genin.christophe.antimonitor.dto.Health;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import org.flywaydb.core.Flyway;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/api/_health")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HealthResource {

    @Inject
    Flyway flyway;

    @Inject
    PgPool pgPool;

    @GET
    public Uni<Health> hello() {
        return pgPool.preparedQuery("select 1 from configuration")
                .execute()
                .map(r -> r.iterator().hasNext())
                .onFailure().recoverWithItem(false)
                .map(dbState -> new Health(flyway.info(), dbState));
    }
}

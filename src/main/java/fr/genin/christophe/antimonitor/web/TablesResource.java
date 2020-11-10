package fr.genin.christophe.antimonitor.web;

import fr.genin.christophe.antimonitor.domain.adapters.Table;
import fr.genin.christophe.antimonitor.services.TableService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
@Path("/api/tables")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TablesResource {

    @Inject
    TableService tableService;

    @GET
    public Multi<JsonObject> findAll() {
        return tableService.findAll().toMulti()
                .flatMap(l -> Multi.createFrom().items(l.stream()))
                .collectItems()
                .asMultiMap(table -> table.tableName)
                .toMulti()
                .flatMap(hm -> Multi.createFrom().items(hm.values().stream()))
                .map(collection -> {
                    final Table table = collection.stream().iterator().next();
                    final String id = collection.stream()
                            .map(t -> t.id)
                            .reduce("", (a, b) -> a + "|" + b);
                    final JsonArray services = new JsonArray(
                            collection.stream()
                                    .map(t -> t.Service)
                                    .collect(Collectors.toList())
                    );
                    return new JsonObject()
                            .put("id", id)
                            .put("name", table.tableName)
                            .put("latestUpdate", table.lastUpdated)
                            .put("services", services);
                });
    }

    @GET
    @Path("/projects")
    public Uni<Map<String, Long>> findByProject(){
        return tableService.countTableByProject();
    }

}

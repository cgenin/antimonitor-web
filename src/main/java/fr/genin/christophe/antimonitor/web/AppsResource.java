package fr.genin.christophe.antimonitor.web;

import fr.genin.christophe.antimonitor.services.AppsService;
import fr.genin.christophe.antimonitor.services.RawService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;

@Path("/api/apps")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AppsResource {

    @Inject
    public AppsService appsService;

    @Inject
    RawService rawService;

    @POST
    public Uni<JsonObject> store(JsonObject json) {
        return Uni
                .createFrom()
                .item(json)
                .map(j-> j.put("updateState", new Date().getTime()))
                .flatMap(a -> appsService.insert(a));
    }

    @GET
    public Multi<JsonObject> findAllOrderbyTime(@QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset) {
        return rawService.findAllOrderbyTime(limit, offset);
    }

    @DELETE
    public Uni<JsonObject> deleteAll(){
        return appsService.deleteAll();
    }

    @DELETE
    @Path("/calculate/datas")
    public Uni<JsonObject> deleteAllCalculate(){
        return appsService.deleteAllCalculate();
    }
}
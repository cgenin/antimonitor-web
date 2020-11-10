package fr.genin.christophe.antimonitor.web;


import fr.genin.christophe.antimonitor.services.ApisService;
import io.smallrye.mutiny.Multi;
import io.vertx.core.json.JsonObject;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Objects;

@ApplicationScoped
@Path("/api/endpoints")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApiResource {

    @Inject
    ApisService apisService;

    @GET
    public Multi<JsonObject> findAll(@QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset) {
        if (Objects.nonNull(limit) || Objects.nonNull(offset)) {
            return apisService.findPaginated(limit, offset);
        }
        return apisService.findAll();
    }

}

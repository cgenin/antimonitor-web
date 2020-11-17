package fr.genin.christophe.antimonitor.web;


import fr.genin.christophe.antimonitor.services.ApisService;
import fr.genin.christophe.antimonitor.services.filter.ApisFilterDto;
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
    public Multi<JsonObject> findAll(@QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset,
                                     @QueryParam("q") String q, @QueryParam("method") String method,
                                     @QueryParam("domain") String domain, @QueryParam("path") String path) {

        final ApisFilterDto apisFilterDto = new ApisFilterDto(q, method, domain, path);

        if (Objects.nonNull(limit) || Objects.nonNull(offset)) {
            return apisService.findPaginatedWithFilter(limit, offset, apisFilterDto);
        }
        return apisService.findAllWithFilter(apisFilterDto);
    }

}

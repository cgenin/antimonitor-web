package fr.genin.christophe.antimonitor.web;

import fr.genin.christophe.antimonitor.domain.adapters.WebApplicationRaw;
import fr.genin.christophe.antimonitor.dto.Apps;
import fr.genin.christophe.antimonitor.dto.RawResult;
import fr.genin.christophe.antimonitor.services.WebAppsService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/fronts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WebAppsResource {

    @Inject
    public WebAppsService webAppsService;


    @POST
    public Uni<Response> store(Apps apps) {
        return Uni
                .createFrom()
                .item(apps)
                .flatMap(a -> webAppsService.store(a))
                .onItem().transform(b -> b ? Response.ok(new RawResult()) : Response.serverError())
                .onItem().transform(Response.ResponseBuilder::build)
                ;
    }

    @GET
    public Multi<WebApplicationRaw> findAll() {
        return webAppsService.findAll();
    }

    @GET
    @Path("/services")
    public Multi<String> findServices() {
        return webAppsService.findServices();
    }


    @GET
    @Path("/by/{domain}")
    public Multi<JsonObject> findByDomain(@PathParam("domain") String domain) {
        return webAppsService.findByDomain(domain);
    }

    @GET
    @Path("/groupby")
    public Multi<JsonObject> groupBy() {
        return webAppsService.groupBy();
    }
}
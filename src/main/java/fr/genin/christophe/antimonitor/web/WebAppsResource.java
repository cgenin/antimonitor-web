package fr.genin.christophe.antimonitor.web;

import fr.genin.christophe.antimonitor.domain.adapters.WebApplicationRaw;
import fr.genin.christophe.antimonitor.dto.Apps;
import fr.genin.christophe.antimonitor.dto.JsonFileDto;
import fr.genin.christophe.antimonitor.dto.RawResult;
import fr.genin.christophe.antimonitor.services.SocketService;
import fr.genin.christophe.antimonitor.services.WebAppsService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Path("/api/fronts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class WebAppsResource {
    private final static Logger LOGGER = LoggerFactory.getLogger(WebAppsResource.class);

    @Inject
    public WebAppsService webAppsService;
    @Inject
    SocketService socketService;

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Uni<Response> storeFile(@MultipartForm JsonFileDto jsonFileDto) {
        LOGGER.info("received " + jsonFileDto);
        try (Scanner scanner = new Scanner(jsonFileDto.file, StandardCharsets.UTF_8.name())) {
            String text = scanner.useDelimiter("\\A").next();
            Apps json = new JsonObject(text).mapTo(Apps.class);
            socketService.sendInfo("Receive new Front file.");
            return Uni
                    .createFrom()
                    .item(json)
                    .flatMap(a -> webAppsService.store(a))
                    .onItem().transform(b -> b ? Response.ok(new RawResult()) : Response.serverError())
                    .onItem().transform(Response.ResponseBuilder::build)

                    .onItem().invoke(() -> {
                        LOGGER.debug("Successfull inserted : " + json);
                        socketService.sendInfo("Inserted front file ok.");
                    })
                    .onFailure().invoke(ex -> {
                        LOGGER.error("An error occured : " + json, ex);
                        socketService.sendError("Error in front file insertion with " + json);
                    });
        } catch (Exception ex) {
            socketService.sendError("Error in deserializing upload");
            throw ex;
        }
    }


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
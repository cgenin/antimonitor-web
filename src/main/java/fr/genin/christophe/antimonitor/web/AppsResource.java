package fr.genin.christophe.antimonitor.web;

import fr.genin.christophe.antimonitor.dto.JsonFileDto;
import fr.genin.christophe.antimonitor.services.AppsService;
import fr.genin.christophe.antimonitor.services.RawService;
import fr.genin.christophe.antimonitor.services.SocketService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Scanner;

@Path("/api/apps")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AppsResource {
    private final static Logger LOGGER = LoggerFactory.getLogger(AppsResource.class);

    @Inject
    public AppsService appsService;

    @Inject
    RawService rawService;

    @Inject
    SocketService socketService;

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Uni<JsonObject> storeFile(@MultipartForm JsonFileDto jsonFileDto) {
        LOGGER.info("received " + jsonFileDto);
        try (Scanner scanner = new Scanner(jsonFileDto.file, StandardCharsets.UTF_8.name())) {
            String text = scanner.useDelimiter("\\A").next();
            final JsonObject json = new JsonObject(text);
            socketService.sendInfo("Receive new Service file.");
            return Uni
                    .createFrom()
                    .item(json)
                    .map(j -> j.put("updateState", new Date().getTime()))
                    .flatMap(a -> appsService.insert(a))
                    .onItem().invoke(() -> {
                        LOGGER.debug("Successfull inserted : " + json);
                        socketService.sendInfo("Inserted service file ok.");
                    })
                    .onFailure().invoke(ex -> {
                        LOGGER.error("An error occured : " + json, ex);
                        socketService.sendError("Error with " + json);
                    });
        } catch (Exception ex) {
            socketService.sendError("Error in deserializing upload");
            throw ex;
        }
    }

    @POST
    public Uni<JsonObject> store(JsonObject json) {
        return Uni
                .createFrom()
                .item(json)
                .map(j -> j.put("updateState", new Date().getTime()))
                .flatMap(a -> appsService.insert(a));
    }


    @GET
    public Multi<JsonObject> findAllOrderbyTime(@QueryParam("limit") Integer limit, @QueryParam("offset") Integer offset) {
        return rawService.findAllOrderbyTime(limit, offset);
    }

    @DELETE
    public Uni<JsonObject> deleteAll() {
        return appsService.deleteAll();
    }

    @DELETE
    @Path("/calculate/datas")
    public Uni<JsonObject> deleteAllCalculate() {
        return appsService.deleteAllCalculate();
    }
}
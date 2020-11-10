package fr.genin.christophe.antimonitor.web;

import fr.genin.christophe.antimonitor.dto.Configuration;
import fr.genin.christophe.antimonitor.services.ConfigurationService;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/api/configuration")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConfigurationResource {

    @Inject
    ConfigurationService configurationService;

    @GET
    public Uni<Configuration> findAll() {
        return configurationService.get();
    }

    @PUT
    public Uni<Configuration> findAll(Configuration configuration) {
        return configurationService.save(configuration)
                .map(b -> configuration);
    }
}

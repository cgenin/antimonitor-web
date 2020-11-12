package fr.genin.christophe.antimonitor.web;


import fr.genin.christophe.antimonitor.services.DependenciesService;
import io.smallrye.mutiny.Multi;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/api/dependencies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DependenciesResource {

    @Inject
    DependenciesService dependenciesService;

    @GET
    public Multi<String> findAll() {
        return dependenciesService.findAllResource();
    }

    @GET
    @Path("/{resource}")
    public Multi<String> usedBy(@PathParam("resource") String resource) {
        return dependenciesService.usedBy(resource);
    }

}

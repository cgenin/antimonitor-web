package fr.genin.christophe.antimonitor.web;

import fr.genin.christophe.antimonitor.services.ProjectService;
import fr.genin.christophe.antimonitor.services.VersionService;
import io.smallrye.mutiny.Multi;
import io.vertx.core.json.JsonObject;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/api/projects")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProjectsResource {

    @Inject
    ProjectService projectService;

    @Inject
    VersionService versionService;

    @GET
    public Multi<JsonObject> findAll(){
        return projectService.findAll()
                .toMulti()
                .flatMap(l->Multi.createFrom().items(l.stream()))
                .map(project-> project.document
                        .put("id", project.id)
                        .put("name", project.name));
    }

    @GET
    @Path("/{id}")
    public Multi<JsonObject> findById(@PathParam("id") String id){
        return versionService.findByIdProject(id);
    }
}

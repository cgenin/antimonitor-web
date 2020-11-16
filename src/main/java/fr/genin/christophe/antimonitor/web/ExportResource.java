package fr.genin.christophe.antimonitor.web;

import fr.genin.christophe.antimonitor.domain.adapters.Dependency;
import fr.genin.christophe.antimonitor.domain.adapters.Project;
import fr.genin.christophe.antimonitor.domain.adapters.Table;
import fr.genin.christophe.antimonitor.domain.adapters.WebApplicationRaw;
import fr.genin.christophe.antimonitor.services.*;
import io.smallrye.mutiny.Multi;
import io.vertx.core.json.JsonObject;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Path("/api/data")
public class ExportResource {

    @Inject
    ProjectService projectService;
    @Inject
    TableService tableService;
    @Inject
    ApisService apisService;
    @Inject
    DependenciesService dependenciesService;

    @Inject
    WebAppsService webAppsService;

    @GET
    @Path("/projects.json")
    public Multi<Project> exportAllProjects() {
        return projectService.findAll()
                .toMulti()
                .flatMap(l -> Multi.createFrom().items(l.stream()));
    }

    @GET
    @Path("/tables.json")
    public Multi<Table> exportAllTabless() {
        return tableService.findAll()
                .toMulti()
                .flatMap(l -> Multi.createFrom().items(l.stream()));
    }

    @GET
    @Path("/apis.json")
    public Multi<JsonObject> exportAllApis() {
        return apisService.findAll();
    }


    @GET
    @Path("/dependencies.json")
    public Multi<Dependency> exportAllDependencies() {
        return dependenciesService.findAll();
    }

    @GET
    @Path("/web-apps.json")
    public Multi<WebApplicationRaw> exportAllWebApps() {
        return webAppsService.findAll();
    }
}

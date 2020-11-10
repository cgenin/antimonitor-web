package fr.genin.christophe.antimonitor.domain;

import fr.genin.christophe.antimonitor.domain.adapters.exception.NoUpdateRequireException;
import fr.genin.christophe.antimonitor.domain.adapters.Project;
import fr.genin.christophe.antimonitor.domain.adapters.Raw;
import fr.genin.christophe.antimonitor.dto.Configuration;
import fr.genin.christophe.antimonitor.services.WebAppsService;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class Projects {
    private final static Logger LOGGER = LoggerFactory.getLogger(WebAppsService.class);


    public Uni<Project> createOrUpdate(Raw raw, List<Project> allProjects, Configuration conf) {
        return Uni.createFrom().item(raw)
                .flatMap(r -> {
                    final String artifactId = r.artifactId();
                    final Project project = allProjects.stream().filter(p -> p.name.equals(artifactId)).findFirst()
                            .orElseGet(() -> new Project(null, artifactId, new JsonObject()));
                    if (r.update >= project.latestUpdate()) {
                        final List<String> allDeps = DomainUtils.extractJavaDeps(r.json);
                        List<String> javaFilters = conf.getJavaFilters();
                        LOGGER.debug("javaFilters:" + javaFilters);
                        final List<String> javaDeps = allDeps.parallelStream()
                                .map(String::toUpperCase)
                                .filter(s ->
                                        javaFilters.isEmpty() ||
                                                javaFilters.parallelStream()
                                                        .map(String::toUpperCase)
                                                        .anyMatch(s::contains)
                                ).collect(Collectors.toList());
                        LOGGER.debug("javaDeps:" + javaDeps);

                        final String version = r.version();
                        if (DomainUtils.isSnapshot(version)) {
                            project.setSnapshot(version);
                        } else {
                            project.setRelease(version);
                        }
                        final List<String> tables = DomainUtils.extractTables(r.json);
                        project.setTables(tables);
                        project.setJavaDeps(javaDeps);
                        Optional.ofNullable(r.json.getString("changelog"))
                                .ifPresent(project::setChangeLog);
                        final List<String> apis = DomainUtils.extractUrls(r.json);
                        project.setApis(apis);
                        LOGGER.info("New data for " + artifactId + ". Document must be updated.");
                        project.setLatestUpdate(r.update);
                        return Uni.createFrom().item(project);

                    }
                    return Uni.createFrom().failure(new NoUpdateRequireException("No item to Update :" + r.id));
                });

    }

}

package fr.genin.christophe.antimonitor.services;

import fr.genin.christophe.antimonitor.DbUtils;
import fr.genin.christophe.antimonitor.domain.adapters.Project;
import fr.genin.christophe.antimonitor.domain.adapters.exception.NoProjectFoundException;
import fr.genin.christophe.antimonitor.jooq.JooqFactory;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowIterator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.function.Function;

import static fr.genin.christophe.antimonitor.jooq.generated.Tables.PROJECTS;

@ApplicationScoped
public class ProjectService {

    public static final Function<Row, Project> ROW_TO_PROJECT = row -> {
        final String id = row.getString(PROJECTS.ID.getName());
        final String name = row.getString(PROJECTS.NAME.getName());
        final JsonObject document = new JsonObject(row.getString(PROJECTS.DOCUMENT.getName()));
        return new Project(id, name, document);
    };
    @Inject
    JooqFactory jooqFactory;

    public Uni<Project> findByArtifact(String artifactId) {
        return jooqFactory.preparedQuery(dsl -> dsl.select(PROJECTS.ID, PROJECTS.NAME, PROJECTS.DOCUMENT)
                .from(PROJECTS)
                .where(PROJECTS.NAME.eq(artifactId))
                .limit(1)
        )
                .flatMap(rowset -> {
                    final RowIterator<Row> iterator = rowset.iterator();
                    if (iterator.hasNext())
                        return Uni.createFrom().item(iterator.next());
                    return Uni.createFrom().failure(new NoProjectFoundException(artifactId));
                })
                .map(ROW_TO_PROJECT);
    }

    public Uni<List<Project>> findAll() {
        return jooqFactory.preparedQuery(dsl -> dsl.select(PROJECTS.ID, PROJECTS.NAME, PROJECTS.DOCUMENT)
                .from(PROJECTS))
                .onItem().transform(DbUtils.rowsetTo(ROW_TO_PROJECT));
    }
}

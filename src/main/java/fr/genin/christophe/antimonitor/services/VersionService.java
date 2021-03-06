package fr.genin.christophe.antimonitor.services;

import fr.genin.christophe.antimonitor.DbUtils;
import fr.genin.christophe.antimonitor.domain.adapters.Version;
import fr.genin.christophe.antimonitor.jooq.JooqFactory;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.sqlclient.Row;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.function.Function;

import static fr.genin.christophe.antimonitor.jooq.generated.Tables.VERSIONS;

@ApplicationScoped
public class VersionService {


    public static final Function<Row, Version> ROW_TO_DOMAIN = row -> {
        final String id = row.getString(VERSIONS.ID.getName());
        final String document = row.getString(VERSIONS.DOCUMENT.getName());
        final String name = row.getString(VERSIONS.NAME.getName());
        final String idProjec = row.getString(VERSIONS.IDPROJECT.getName());
        return new Version(id, new JsonObject(document), name, idProjec);
    };
    @Inject
    JooqFactory jooqFactory;


    public Uni<List<Version>> findByIdProjectAndVersion(String idProject, String version) {
        return jooqFactory.preparedQuery(dsl -> dsl.select()
                .from(VERSIONS)
                .where(VERSIONS.IDPROJECT.eq(idProject).and(VERSIONS.NAME.eq(version)))
                .limit(1)
        )
                .toMulti()
                .flatMap(DbUtils.ROWSET_TO_MULTI_ROW)
                .map(ROW_TO_DOMAIN)
                .collectItems().asList();
    }

    public Multi<JsonObject> findByIdProject(String idProject) {
        return jooqFactory.preparedQuery(dsl -> dsl.select()
                .from(VERSIONS)
                .where(VERSIONS.IDPROJECT.eq(idProject))
                .orderBy(VERSIONS.NAME.desc())
        ).toMulti()
                .flatMap(DbUtils.ROWSET_TO_MULTI_ROW)
                .map(row -> {
                    final String id = row.getString(VERSIONS.ID.getName());
                    final String document = row.getString(VERSIONS.DOCUMENT.getName());
                    final String name = row.getString(VERSIONS.NAME.getName());
                    return new JsonObject(document).put("name", name).put("id", id);
                });
    }
}

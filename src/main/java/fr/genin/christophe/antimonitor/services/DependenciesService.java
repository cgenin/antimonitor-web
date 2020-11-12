package fr.genin.christophe.antimonitor.services;

import fr.genin.christophe.antimonitor.DbUtils;
import fr.genin.christophe.antimonitor.domain.adapters.Dependency;
import fr.genin.christophe.antimonitor.jooq.JooqFactory;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static fr.genin.christophe.antimonitor.jooq.generated.Tables.DEPENDENCIES;

@ApplicationScoped
public class DependenciesService {

    @Inject
    JooqFactory jooqFactory;

    public Multi<Dependency> findAll() {
        return jooqFactory.preparedQuery(dsl->
                dsl.select(DEPENDENCIES.RESOURCE, DEPENDENCIES.USED_BY)
                        .from(DEPENDENCIES)

        ).toMulti()
                .flatMap(DbUtils.ROWSET_TO_MULTI_ROW)
                .map(row ->{
                    final String resource = row.getString(DEPENDENCIES.RESOURCE.getName());
                    final String usedBy = row.getString(DEPENDENCIES.USED_BY.getName());
                    return new Dependency(resource, usedBy) ;
                });
    }

    public Uni<Boolean> deleteByUsedBy(String usedBy) {
        return jooqFactory.preparedQuery(dsl ->
                dsl.delete(DEPENDENCIES).where(DEPENDENCIES.USED_BY.eq(usedBy))
        )
                .map(rowset -> rowset.rowCount() > 0);
    }

    public Multi<String> findAllResource(){
        return jooqFactory.preparedQuery(dsl->
                dsl.select(DEPENDENCIES.RESOURCE).from(DEPENDENCIES).groupBy(DEPENDENCIES.RESOURCE).orderBy(DEPENDENCIES.RESOURCE)
        ).toMulti()
                .flatMap(DbUtils.ROWSET_TO_MULTI_ROW)
                .map(row->row.getString(DEPENDENCIES.RESOURCE.getName()));
    }

    public Multi<String> usedBy(String resource){
        return jooqFactory.preparedQuery(dsl->
                dsl.select(DEPENDENCIES.USED_BY).from(DEPENDENCIES)
                        .where(DEPENDENCIES.RESOURCE.eq(resource))
                        .groupBy(DEPENDENCIES.USED_BY)
                        .orderBy(DEPENDENCIES.USED_BY)
        ).toMulti()
                .flatMap(DbUtils.ROWSET_TO_MULTI_ROW)
                .map(row->row.getString(DEPENDENCIES.USED_BY.getName()));
    }
}

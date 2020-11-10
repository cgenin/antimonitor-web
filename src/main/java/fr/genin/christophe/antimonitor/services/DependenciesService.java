package fr.genin.christophe.antimonitor.services;

import fr.genin.christophe.antimonitor.jooq.JooqFactory;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static fr.genin.christophe.antimonitor.jooq.generated.Tables.DEPENDENCIES;

@ApplicationScoped
public class DependenciesService {

    @Inject
    JooqFactory jooqFactory;

    public Uni<Boolean> deleteByUsedBy(String usedBy) {
        return jooqFactory.preparedQuery(dsl ->
                dsl.delete(DEPENDENCIES).where(DEPENDENCIES.USED_BY.eq(usedBy))
        )
                .map(rowset -> rowset.rowCount() > 0);
    }
}

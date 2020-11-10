package fr.genin.christophe.antimonitor.services;

import fr.genin.christophe.antimonitor.dto.Configuration;
import fr.genin.christophe.antimonitor.jooq.JooqFactory;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonObject;
import org.jooq.JSON;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static fr.genin.christophe.antimonitor.jooq.generated.Tables.CONFIGURATION;

@ApplicationScoped
public class ConfigurationService {


    public static final int ID_CONFIGURATION = 1;
    @Inject
    JooqFactory jooqFactory;

    public Uni<Boolean> save(Configuration configuration) {
        return Uni.createFrom().item(configuration)
                .map(JsonObject::mapFrom)
                .map(JsonObject::encode)
                .map(JSON::json)
                .flatMap(c -> jooqFactory.preparedQuery(dsl -> dsl
                                .update(CONFIGURATION)
                                .set(CONFIGURATION.CONFIGURATION_, c)
                                .where(CONFIGURATION.ID.eq(ID_CONFIGURATION))
                        )
                )
                .map(r -> r.rowCount() > 0);
    }

    public Uni<Configuration> get() {
        return jooqFactory.preparedQuery(dsl -> dsl.select(CONFIGURATION.CONFIGURATION_)
                .from(CONFIGURATION)
                .where(CONFIGURATION.ID.eq(ID_CONFIGURATION))
        )
                .map(row -> row.iterator().next())
                .onItem().ifNotNull()
                .transform(row -> row.getValue(CONFIGURATION.CONFIGURATION_.getName()).toString())
                .map(JsonObject::new)
                .map(jsonObject -> jsonObject.mapTo(Configuration.class));
    }

}

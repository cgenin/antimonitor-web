package fr.genin.christophe.antimonitor.domain;

import fr.genin.christophe.antimonitor.domain.adapters.StateCompletion;
import fr.genin.christophe.antimonitor.domain.adapters.WebApplicationRaw;
import fr.genin.christophe.antimonitor.domain.output.WebApplicationInsert;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDateTime;
import java.util.UUID;

@ApplicationScoped
public class WebApplications {

    public Uni<Boolean> insert(WebApplicationRaw war, WebApplicationInsert dao) {
        war.id = UUID.randomUUID().toString();
        war.state = StateCompletion.inserted;
        war.creationDate = LocalDateTime.now();
        return dao.insert(war);
    }
}

package fr.genin.christophe.antimonitor.domain.output;

import fr.genin.christophe.antimonitor.domain.adapters.WebApplicationRaw;
import io.smallrye.mutiny.Uni;

public interface WebApplicationInsert {

    Uni<Boolean> insert(WebApplicationRaw war);

}

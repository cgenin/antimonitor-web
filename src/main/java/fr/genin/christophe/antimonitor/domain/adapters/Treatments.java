package fr.genin.christophe.antimonitor.domain.adapters;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * State of all tasks for all differents screens.
 * <p>Contains also methods for launching periodic treatment</p>
 */
public enum Treatments {
    PROJECTS(0),
    TABLES(1),
    VERSION(2),
    URL(3),
    DEPENDENCIES(4),
    END(5);

    private static final Logger logger = LoggerFactory.getLogger(Treatments.class);

    private final Integer state;

    Treatments(Integer state) {
        this.state = state;
    }

    public Integer getState() {
        return state;
    }


    public static Treatments parse(Integer state) {
        return Arrays.stream(Treatments.values())
                .filter(treatments -> treatments.getState().equals(state))
                .findFirst()
                .orElseGet(() -> {
                    logger.warn("Treatments not found : " + state + " return end by default");
                    return END;
                });
    }

}

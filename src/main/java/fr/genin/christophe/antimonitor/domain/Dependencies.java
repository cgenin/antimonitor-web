package fr.genin.christophe.antimonitor.domain;

import fr.genin.christophe.antimonitor.domain.adapters.Dependency;
import fr.genin.christophe.antimonitor.domain.adapters.Project;
import fr.genin.christophe.antimonitor.domain.adapters.Raw;
import io.smallrye.mutiny.Multi;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.regex.Pattern;

@ApplicationScoped
public class Dependencies {

    public static String format(String str) {
        return new DependenciesSanitizer(str).run();
    }


    public Multi<Dependency> createFrom(Raw raw, Project project) {
        String usedBy = format(raw.artifactId());

        return Multi.createFrom().items(project.javaDeps().stream())
                .map(Dependencies::format)
                .filter(resource -> !"STARTER".equals(resource))
                .transform().byDroppingDuplicates()
                .map(resource -> new Dependency(resource, usedBy));
    }

    static class DependenciesSanitizer {

        private static final Pattern[] PATTERNS = new Pattern[]{
                Pattern.compile("-MANAGER"),
                Pattern.compile("-SERVICE"),
                Pattern.compile("-IMPL"),
                Pattern.compile("-CLIENT"),
        };
        private final String str;

        private DependenciesSanitizer(String str) {
            this.str = str;
        }

        private String innerRun(String chaine, Pattern[] regexps) {
            if (regexps.length == 0) {
                return chaine;
            }
            String newChaine = regexps[0].matcher(chaine).replaceAll("");
            Pattern[] newPatterns = (regexps.length == 1) ? new Pattern[0] : Arrays.copyOfRange(regexps, 1, regexps.length);
            return innerRun(newChaine, newPatterns);

        }

        public String run() {
            String US = str.toUpperCase();
            return innerRun(US, PATTERNS);
        }
    }
}

package fr.genin.christophe.antimonitor.domain;

import fr.genin.christophe.antimonitor.domain.adapters.Raw;
import fr.genin.christophe.antimonitor.domain.adapters.Table;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class Tables {

    public Multi<Table> getTableToCreate(Raw raw, List<Table> existingTables) {
        final List<String> tables = extractNames(raw);
        final List<Table> tableForService = filterByServices(raw, existingTables);
        if (tableForService.isEmpty()) {
            return Multi.createFrom().items(tables.stream())
                    .map(nameToNewTable(raw));
        }
        return Uni.createFrom().item(tables)
                .map(HashSet::new)
                .toMulti()
                .flatMap(ts -> {
                    ts.removeAll(tableForService.stream().map(t -> t.tableName).collect(Collectors.toList()));
                    return Multi.createFrom().items(ts.stream());
                })
                .map(nameToNewTable(raw));

    }

    private List<Table> filterByServices(Raw raw, List<Table> existingTables) {
        final String artifactId = raw.artifactId();
        return existingTables.stream()
                .filter(t -> t.Service.equals(artifactId))
                .collect(Collectors.toList());
    }

    public Multi<String> deletionId(Raw raw, List<Table> existingTables) {
        final List<Table> tableForService = filterByServices(raw, existingTables);
        if (tableForService.isEmpty()) {
            return Multi.createFrom().items(Stream.empty());
        }
        final List<String> tables = extractNames(raw);
        return Multi.createFrom().items(tableForService.stream())
                .flatMap(table -> {
                    final boolean isPresent = tables.stream().anyMatch(name -> name.equals(table.tableName));
                    if (isPresent) {
                        return Multi.createFrom().items(Stream.empty());
                    }

                    return Multi.createFrom().item(table);
                })
                .map(t -> t.id);
    }

    private List<String> extractNames(Raw raw) {
        return raw.json.getJsonArray("tables", new JsonArray()).stream()
                .map(o -> (JsonObject) o)
                .map(js -> js.getString("table", ""))
                .collect(Collectors.toList());
    }

    private Function<String, Table> nameToNewTable(Raw raw) {
        return name -> new Table(UUID.randomUUID().toString(), raw.artifactId(), name, raw.update);
    }
}

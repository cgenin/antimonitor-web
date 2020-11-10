package fr.genin.christophe.antimonitor.domain;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class DomainUtils {

    private DomainUtils(){}


    public static boolean isSnapshot(String version) {
        return version.contains("SNAPSHOT");
    }

    public static List<String> extractJavaDeps(JsonObject json) {
        JsonArray jsonArray = json.getJsonArray("dependencies", new JsonArray());
        return jsonArray.stream()
                .map(o -> (JsonObject) o)
                .map(js -> js.getString("artifactId", ""))
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<String> extractUrls(JsonObject json) {
        JsonObject apis = json.getJsonObject("apis", new JsonObject());
        JsonArray services = apis.getJsonArray("services", new JsonArray());


        return services.stream()
                .map(o -> (JsonObject) o)
                .map(js -> js.getJsonArray("methods", new JsonArray()))
                .flatMap(JsonArray::stream)
                .map(o -> (JsonObject) o)
                .map(js -> {
                    final String method = js.getString("method", "");
                    final String path = js.getString("path", "");
                    return method + " - " + path;
                })
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Extract from Json Object the name of the tables.
     *
     * @param json the Json
     * @return the list of table's name.
     */
    public static List<String> extractTables(JsonObject json) {
        return json.getJsonArray("tables", new JsonArray())
                .stream()
                .map(o -> (JsonObject) o)
                .map(js -> js.getString("table", ""))
                .distinct()
                .collect(Collectors.toList());
    }


}

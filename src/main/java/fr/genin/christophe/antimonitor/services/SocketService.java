package fr.genin.christophe.antimonitor.services;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Date;

@ApplicationScoped
public class SocketService {

    @Inject
    Vertx vertx;

    public static final String CONSOLE = "console.text";

    public void sendInfo(String msg) {
        long date = new Date().getTime();
        vertx.eventBus().publish(SocketService.CONSOLE, new JsonObject()
                .put("msg", msg)
                .put("date", date)
                .put("type", "info"));
    }

    public void sendError(String msg) {
        long date = new Date().getTime();
        vertx.eventBus().publish(SocketService.CONSOLE, new JsonObject()
                .put("msg", msg)
                .put("date", date)
                .put("type", "error"));
    }
}

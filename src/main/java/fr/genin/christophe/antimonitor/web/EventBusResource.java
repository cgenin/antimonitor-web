package fr.genin.christophe.antimonitor.web;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.util.Date;

@ApplicationScoped
public class EventBusResource {
    static final String CONSOLE = "console.text";
    @Inject
    Vertx vertx;


    public void initialize(@Observes Router router) {
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        sockJSHandler.bridge(
                new SockJSBridgeOptions()
                        .addOutboundPermitted(new PermittedOptions().setAddress(CONSOLE))
        );
        router.route("/eventbus/*").handler(sockJSHandler);
    }

    public void sendMessage(String msg) {
            long date = new Date().getTime();
            vertx.eventBus().publish(CONSOLE, new JsonObject()
                    .put("msg", msg)
                    .put("date", date)
                    .put("type", "info"));
    }

}

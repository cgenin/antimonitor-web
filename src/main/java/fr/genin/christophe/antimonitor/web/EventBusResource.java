package fr.genin.christophe.antimonitor.web;

import fr.genin.christophe.antimonitor.services.SocketService;
import io.vertx.core.Vertx;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.core.HttpHeaders;

@ApplicationScoped
public class EventBusResource {
    @Inject
    Vertx vertx;


    public void initialize(@Observes Router router) {
        router.get("/").handler(ctx -> {
            ctx.response().putHeader(HttpHeaders.CACHE_CONTROL, "no-store");
            ctx.next();
        });

        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        sockJSHandler.bridge(
                new SockJSBridgeOptions()
                        .addOutboundPermitted(new PermittedOptions().setAddress(SocketService.CONSOLE))
        );
        router.route("/eventbus/*").handler(sockJSHandler);
    }


}

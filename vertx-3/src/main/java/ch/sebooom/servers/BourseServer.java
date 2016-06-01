package ch.sebooom.servers;


import bourse.Indices;
import ch.sebooom.EventBusAdress;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.core.http.ServerWebSocket;
import io.vertx.rxjava.ext.web.Router;

import java.util.ArrayList;
import java.util.List;

/**
 * Vertx web server.
 * Paramètres de configuration:
 * http.port = port tcp d' écoute du serveur http
 */
public class BourseServer extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(BourseServer.class);
    public static final CharSequence H1_TEST_MESSAGE = "<h1>test : ok </h1>";
    private int port;
    public static final int DEFAULT_PORT = 8989;
    private static final String TAG = "[WSBourseServer]";
    private static final String TAG_EB = "[WSBourseServer EB Read]";
    List<ServerWebSocket> client = new ArrayList<>();

    @Override
    public void start() {

        Router router = Router.router(vertx);

        serverConfig();

        HttpServer server = vertx.createHttpServer().requestHandler(router::accept);

        log.info(TAG  + "[" + Thread.currentThread().getName()
                        +"] Creating httpServer on port [" + port + "]");


        addWebSocketHandler(server);

        initRoutes(router);

        startServerListenning(server);

    }

    private void serverConfig() {

        Integer http_port = this.config().getInteger("http.port");
        port = (null == http_port) ? DEFAULT_PORT : http_port;
    }


    private void addWebSocketHandler (HttpServer server) {

        vertx.eventBus().consumer(EventBusAdress.INDICES_RANDOM.adress(), message -> {
            log.info(TAG_EB + "EventuBus msg : " + message.body());
            //envoi sur flux websocket
            //wsStream.writeFinalTextFrame(message.body().toString());
            sendToWebSocketClients(message.body().toString());
        });

        vertx.eventBus().consumer(EventBusAdress.ACTIONS_RANDOM.adress(), message -> {
            log.info(TAG_EB + "EventuBus msg : " + message.body());
            //envoi sur flux websocket
            sendToWebSocketClients(message.body().toString());
        });

        //Connexion client au websocket
        server.
            websocketStream()
            .toObservable()
            .subscribe(wsStream -> {

                String clientInfo = wsStream.localAddress().host()
                        + ":" + wsStream.localAddress().port();

                log.info(TAG + "WebSocket client connect : " + clientInfo);

                wsStream.closeHandler(handler -> {
                    client.remove(wsStream);
                    log.error(TAG + "Client disconnect : " + clientInfo);

                });

                client.add(wsStream);

            });

        }

    private void sendToWebSocketClients(String msg) {

        client.forEach(webSocketClient -> {
            log.info(TAG + "Message send to client on websocket");
            webSocketClient.writeFinalTextFrame(msg);
        });

    }

    private void startServerListenning (HttpServer server) {

        server.listen(port);

    }





    private void initRoutes (Router router) {
        //test
        router.get(Paths.TEST.path()).handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end(H1_TEST_MESSAGE.toString());
        });

        //indices
        router.get(Paths.API_INDICES.path()).handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "application/json")
                    .end(Json.encode(Indices.getIndicesAleatoire()));
        });
    }
}

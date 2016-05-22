package ch.sebooom.servers;


import ch.sebooom.worker.WebSocketWriter;
import ch.sebooom.worker.cotation.WebSocketCotationWriter;
import ch.sebooom.worker.position.WebSocketPositionWriter;
import cotation.Indices;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.core.http.ServerWebSocket;
import io.vertx.rxjava.ext.web.Router;
import rx.Observable;

import static ch.sebooom.servers.Paths.COTATION_WEBSOCKET;
import static ch.sebooom.servers.Paths.GPS_WEBSOCKET;
import static ch.sebooom.servers.Paths.isPathForType;

/**
 * Vertx web server.
 * Paramètres de configuration:
 * http.port = port tcp d' écoute du serveur http
 */
public class WebServer extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(WebServer.class);
    public static final CharSequence H1_TEST_MESSAGE = "<h1>test : ok </h1>";
    private int port;
    public static final int DEFAULT_PORT = 8989;


    @Override
    public void start() {

        Router router = Router.router(vertx);

        serverConfig();

        HttpServer server = vertx.createHttpServer().requestHandler(router::accept);

        log.info("[" +Thread.currentThread().getName()
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

        Observable<ServerWebSocket> socketObservable = server.websocketStream().toObservable();

        socketObservable
                .filter(item->{

                    log.info("[test] - " + item.path() + " " + isPathForType(item.path(), PathType.WEBSOCKET));
                    return isPathForType(item.path(), PathType.WEBSOCKET);
                })
                .subscribe(
                        socket -> {
                            log.info("[webserver] Web socket connect [" + socket.path()+ "] - " + socket.binaryHandlerID());

                            vertx.executeBlockingObservable(handler ->{

                                startWorkerWriter(socket);

                                socket.closeHandler(closeHandler -> {
                                    log.info("[webserver] Socket closed by client");
                                });

                            });
                        },
                        failure -> {
                            log.error("[webserver] Error during starting websocket [" + failure.getCause() + "]");
                        },
                        () -> {
                            log.info("[webserver] Subscription ended or server closed");
                        }
                );

    }

    private void startServerListenning (HttpServer server) {

        server.listen(port);

    }




    private WebSocketWriter startWorkerWriter (ServerWebSocket socket) {

        WebSocketWriter socketWriter;

        log.info("Starting worker:" + socket.path());

        if(socket.path().equals(GPS_WEBSOCKET.path())){
            socketWriter = new WebSocketPositionWriter().socket(socket);
        }else if(socket.path().equals(COTATION_WEBSOCKET.path())){
            socketWriter = new WebSocketCotationWriter().socket(socket);
        }else{
            throw new IllegalArgumentException("Socket path illegal: " + socket.path());
        }

        socketWriter.start();

        return socketWriter;
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

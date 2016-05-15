package ch.sebooom.servers;


import ch.sebooom.worker.WebSocketWriter;
import ch.sebooom.worker.cotation.WebSocketCotationWriter;
import ch.sebooom.worker.position.WebSocketPositionWriter;
import cotation.Indices;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.core.http.HttpServerResponse;
import io.vertx.rxjava.core.http.ServerWebSocket;
import io.vertx.rxjava.ext.web.Router;
import rx.Observable;

/**
 * Created by seb on 01.05.16.
 */
public class WebServer extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(WebServer.class);
    private Integer port;
    private static final int DEFAULT_PORT = 8989;
    public static final String H1_TEST_MESSAGE = "Test ok";

    enum WebSocketPaths{
        GPS_WEBSOCKET("/ws/gps"),
        COTATION_WEBSOCKET("/ws/cot");

        private final String path;

        WebSocketPaths(String path){
            this.path = path;
        }

        public String path() {
            return path;
        }

    }

    Vertx vertx = Vertx.vertx();


    public void start() {

        Router router = Router.router(vertx);

        port = this.config().getInteger("http.port");

        if(null == port){
            port = DEFAULT_PORT;
        }


        HttpServer server = vertx.createHttpServer().requestHandler(router::accept);



        log.info("[" +Thread.currentThread().getName()
                        +"] Creating httpServer on port [" + port + "]");


        addWebSocketHandler(server);

        startServerListenning(server);

        initRoutes(router);


    }



    private void addWebSocketHandler (HttpServer server) {

        Observable<ServerWebSocket> socketObservable = server.websocketStream().toObservable();

        socketObservable
                .filter(item->{
                    return item.path().equals(WebSocketPaths.GPS_WEBSOCKET.path())
                            || item.path().equals(WebSocketPaths.COTATION_WEBSOCKET.path());
                })
                .subscribe(
                        socket -> {
                            log.info("Web socket connect [" + socket.binaryHandlerID() + "]");

                            vertx.executeBlockingObservable(handler ->{

                                startWorkerWriter(socket);

                                socket.closeHandler(closeHandler -> {
                                    log.info("Socket closed by client");
                                });

                            });
                        },
                        failure -> {
                            log.info("Error during starting websocket [" + failure.getCause() + "]");
                        },
                        () -> {
                            log.info("Subscription ended or server closed");
                        }
                );

    }

    private void startServerListenning (HttpServer server) {

        server.listen(port);

    }




    private WebSocketWriter startWorkerWriter (ServerWebSocket socket) {

        WebSocketWriter socketWriter;

        log.info("Starting worker:" + socket.path());

        if(socket.path().equals(WebSocketPaths.GPS_WEBSOCKET.path())){
            socketWriter = new WebSocketPositionWriter().socket(socket);
        }else if(socket.path().equals(WebSocketPaths.COTATION_WEBSOCKET.path())){
            socketWriter = new WebSocketCotationWriter().socket(socket);
        }else{
            throw new IllegalArgumentException("Socket path illegal: " + socket.path());
        }

        socketWriter.start();

        return socketWriter;
    }

    private void initRoutes (Router router) {
        //test
        router.get("/test").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "text/html")
                    .end("<h1>" + H1_TEST_MESSAGE +"</h1>");
        });

        //indices
        router.get("/api/indices").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response
                    .putHeader("content-type", "application/json")
                    .end(Json.encode(Indices.getIndicesAleatoire()));
        });
    }
}

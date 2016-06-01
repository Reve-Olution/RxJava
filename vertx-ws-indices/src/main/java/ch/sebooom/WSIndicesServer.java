package ch.sebooom;

import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.ext.web.Router;

/**
 * Created by seb on 22.05.16.
 */
public class WSIndicesServer extends AbstractVerticle {

    private static final Logger log = LoggerFactory.getLogger(WSIndicesServer.class);
    private static final String TAG = "[WSIndicesServer] - ";

    @Override
    public void start() throws Exception {

        Integer http_port = getHttpPort();
        log.info(TAG + " Starting WSIndicesServerVerticles on port : " + http_port);

        HttpServer server = vertx.createHttpServer(
                new HttpServerOptions().setPort(http_port).setHost("localhost")
        );

        Router route = initRoutes();

        startWebSocket(server);

        //server boot
        server.requestHandler(route::accept).listenObservable()
                .subscribe(
                srv -> {
                    log.info(TAG + "WSIndicesServer [" + srv +"] started on port : " + http_port);
                }
        );

    }

    private void startWebSocket (HttpServer server) {
        server.
                websocketStream().
                toObservable()
                .subscribe(wsStream -> {
                    log.info(TAG + "WSSocketStream ready : " + wsStream.textHandlerID());



                    //lecture des messages sur l'eb
                    vertx.eventBus().consumer(EventBusAdress.INDICES_RANDOM.adress(),message -> {
                        log.info(TAG + "From eb : " + message.body());
                        wsStream.writeFinalTextFrame(message.body().toString());
                    });


                },error -> {
                    log.error(TAG + "Error during websocket stream listening : " +  error);
                    log.error(TAG + "System will now shut down....");
                    System.exit(1);
                },()->{

                });
    }

    private Router initRoutes () {
        Router route = Router.router(vertx);

        route.get("/api/indices/last").handler(handler -> {
            //handler.response().end("/api/indices/last");
            //requete avec reply sur eb pour listes etats indices
            vertx.eventBus().send(EventBusAdress.INDICES_ALL.adress(),"get",reply -> {
                if (reply.succeeded()) {

                    handler.response().end(reply.result().body().toString());
                }else{
                    handler.response().end("error....");
                }
            });
        });

        route.get("/api/indices/all").handler(handler -> {
            handler.response().end("/api/indices/all");
        });

        route.get("/api/indices/id").handler(handler -> {
            handler.response().end("/api/indices/id");
        });

        return route;
    }

    private Integer getHttpPort() {
        Integer http_port = this.config().getInteger("http.port");

        if(null == http_port){
            throw new IllegalArgumentException("The parameter http.port muste be defined in vericle config");
        }
        log.info(Thread.currentThread().getName() + " : starting server port : "  + http_port);
        return http_port;
    }
}

package ch.sebooom;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;

/**
 * Classe de démarrage du projet.
 * --> WebServer avec WebSocket et point d'entrées REST
 * --> PORT 8888 par déafut
 */
public class Starter {

    private static final Logger log = LoggerFactory.getLogger(Starter.class);
    private static final String TAG = "[Starter] - ";
    private static Vertx vertx = null;

    public static void main(String[] args) {


        //options de déploiements
        DeploymentOptions options = new DeploymentOptions();
        options.setConfig(new JsonObject().put("http.port", 8888));
        options.setHa(true);

        //options 'dinstances vertx
        VertxOptions voptions = new VertxOptions();
        voptions.setClusterHost("127.0.0.1");
        voptions.setHAEnabled(true);

        //déploiement du cluster
        Vertx.clusteredVertx(voptions, result -> {

            if (result.succeeded()) {
                vertx = result.result();

                //déploiements des 2 verticles du projets
                //Server (rest + ws)
                Observable<String> webserverObs = vertx.deployVerticleObservable(WSIndicesServer.class.getName(), options);
                //MarketIndex Flow Generator
                Observable<String> dataFlowObs = vertx.deployVerticleObservable(MarketIndexSampleFlow.class.getName(), options);


                Observable.merge(webserverObs,dataFlowObs)
                        .subscribe(
                                instance -> {
                                    log.info(TAG + "Verticle successfully deployed : "  + instance);
                                },
                                error -> {
                                    log.info(TAG + error.getMessage());
                                    error.printStackTrace();
                                    log.info(TAG + "System will shutdown");
                                    System.exit(1);
                                },
                                () -> {
                                    log.info(TAG + " All verticles deployed!");
                                }
                        );
            } else {
                log.error(TAG + "Vertx Failed: " + result.cause());
            }
        });




    }
}



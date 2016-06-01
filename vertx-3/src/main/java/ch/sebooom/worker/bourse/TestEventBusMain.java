package ch.sebooom.worker.bourse;

import io.vertx.core.VertxOptions;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;

/**
 * Created by seb on 24.05.16.
 */
public class TestEventBusMain {

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();

        //options 'dinstances vertx
        VertxOptions voptions = new VertxOptions();
        voptions.setClusterHost("127.0.0.1");
        voptions.setHAEnabled(true);


        vertx.clusteredVertx(voptions, result -> {

            if (result.succeeded()) {
                Vertx vertxClustered = result.result();

                //déploiements des 2 verticles du projets
                //Server (rest + ws)
                Observable<String> webserverObs = vertxClustered.deployVerticleObservable(TestEventBus.class.getName());
                //MarketIndex Flow Generator
                Observable<String> dataFlowObs = vertxClustered.deployVerticleObservable(IndicesEBDataGeneratorVerticle.class.getName());


            }

        });
    }
}

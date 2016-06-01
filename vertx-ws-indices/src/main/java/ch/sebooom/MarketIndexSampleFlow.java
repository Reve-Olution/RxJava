package ch.sebooom;

import bourse.ValeurBoursiere;
import bourse.Indices;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.AbstractVerticle;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static ch.sebooom.EventBusAdress.INDICES_RANDOM;

/**
 * Verticle destiné à envoyer des message sur l' eventbus
 * Les messages correspondent en une génération aléatoire de ours d'indinces boursiers
 */
public class MarketIndexSampleFlow extends AbstractVerticle{


    List<ValeurBoursiere> valeurBoursieres = Indices.getIndicesAsList();
    private static final Logger log = LoggerFactory.getLogger(MarketIndexSampleFlow.class);
    private static final String TAG = "[MarketIndeSampleFlow] - ";




    @Override
    public void start () {

                vertx.eventBus().consumer(EventBusAdress.INDICES_ALL.adress())
                        .toObservable()
                        .subscribe(get -> {
                            //msg == get
                            if(get.body().equals("get")){
                                get.reply(Json.encode(valeurBoursieres));
                            }
                        });

                Executor executor = Executors.newSingleThreadExecutor();

                executor.execute(() -> {


                    while (true) {
                        ValeurBoursiere valeurBoursiere = getIndiceAlea();
                        vertx.eventBus().send(INDICES_RANDOM.adress(), Json.encode(valeurBoursiere));
                        //vertx.eventBus().send(INDICES_ALL.adress(), Json.encode(indices));
                        log.info(TAG + addLogprefix(Thread.currentThread().getName() + " : sending to bus: " + Json.encode(valeurBoursiere)));
                        valeurBoursiere.nextAleatoire();
                        try {
                            Thread.sleep(getRandomMillisForThreadSleep(30, 200));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


                });

    }

    protected ValeurBoursiere getIndiceAlea () {
        int maxInclus = valeurBoursieres.size();

        Integer i = new Random().nextInt(maxInclus);

        return valeurBoursieres.get(i);
    }

    protected int getRandomMillisForThreadSleep(int min, int max){

        int threadSleepMillis = 0;

        while(threadSleepMillis < min || threadSleepMillis > max){
            threadSleepMillis = (int) (Math.random() * max);
        }

        return threadSleepMillis;
    }

    private String addLogprefix (String msg) {
        return "[" + this.getClass().getCanonicalName() + " - " + Thread.currentThread().getName() +"] --> " + msg;
    }

}

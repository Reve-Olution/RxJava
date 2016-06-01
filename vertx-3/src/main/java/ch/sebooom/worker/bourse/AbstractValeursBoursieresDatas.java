package ch.sebooom.worker.bourse;

import bourse.ValeurBoursiere;
import ch.sebooom.EventBusAdress;
import io.vertx.core.json.Json;
import io.vertx.rxjava.core.AbstractVerticle;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by seb on 25.05.16.
 */
public abstract class AbstractValeursBoursieresDatas extends AbstractVerticle {

    protected List<ValeurBoursiere> valeurBoursieres = null;

    @Override
    public void start() throws Exception {
        super.start();
    }

    protected void initData() {
        valeurBoursieres = getIndicesAsList();
    }

    /**
     * Retourne un indice présent dans la liste de manière aléatoire
     * @return un indice présent dans la liste
     */
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

    protected String addLogprefix (String msg) {
        return "[" + this.getClass().getCanonicalName() + " - " + Thread.currentThread().getName() +"] --> " + msg;
    }

    /**
     * Retourne une liste d'indices servant d'exemples de données-
     * @return une liste d'instance Indice
     */
    abstract List<ValeurBoursiere> getIndicesAsList () ;

    abstract void startEventBusListenning();

    protected void startWebSocketFlow(EventBusAdress adresseFluxEventBus) {
        Executor executor = Executors.newSingleThreadExecutor();

        //job d'envoi des messages sur le bus 1 message d'un indice
        executor.execute(() -> {


            while (true) {
                ValeurBoursiere valeurBoursiere = getIndiceAlea();
                vertx.eventBus().publish(adresseFluxEventBus.adress(), Json.encode(valeurBoursiere));
                valeurBoursiere.nextAleatoire();
                try {
                    Thread.sleep(getRandomMillisForThreadSleep(30, 200));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        });
    }
}

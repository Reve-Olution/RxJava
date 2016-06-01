package ch.sebooom.worker.bourse;

import bourse.Cours;
import bourse.Pays;
import bourse.TypeValeurs;
import bourse.ValeurBoursiere;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static ch.sebooom.EventBusAdress.ACTIONS_ALL;
import static ch.sebooom.EventBusAdress.ACTIONS_RANDOM;

/**
 * Created by seb on 24.05.16.
 */
public class ActionsEBDataGeneratorVerticle extends AbstractValeursBoursieresDatas{

    private static final Logger log = LoggerFactory.getLogger(ActionsEBDataGeneratorVerticle.class);
    private static final String TAG = "[ActionsEBDataGeneratorVerticle] - ";
    private static final String GET = "get";


    @Override
    public void start () {

        initData();

        startEventBusListenning();

        startWebSocketFlow(ACTIONS_RANDOM);

    }

    void startEventBusListenning() {
        vertx.eventBus()
                .consumer(ACTIONS_ALL.adress())
                .toObservable()
                .subscribe(get -> {
                    //msg == get
                    if(get.body().equals(GET)){
                        get.reply(Json.encode(valeurBoursieres));
                    }
                });
    }

    /**
     * Retourne une liste d'indices servant d'exemples de données-
     * @return une liste d'instance Indice
     */
    public List<ValeurBoursiere> getIndicesAsList () {

        List<ValeurBoursiere> valeurBoursieres = new ArrayList<>();

        valeurBoursieres.add(new ValeurBoursiere("DU PONT NEMOURS&CO","DU PONT NEMOURSCO",new Cours(67.01),Pays.FRANCE,TypeValeurs.ACTION));
        valeurBoursieres.add(new ValeurBoursiere("APPLE","APPLE",new Cours(96.42),Pays.USA,TypeValeurs.ACTION));
        valeurBoursieres.add(new ValeurBoursiere("GOLDMAN SACHS GROUP","GOLDMAN SACHS GROUP",new Cours(155.58),Pays.USA,TypeValeurs.ACTION));
        valeurBoursieres.add(new ValeurBoursiere("CATERPILLAR","CATERPILLAR",new Cours(70.41),Pays.ALLEMAGNE,TypeValeurs.ACTION));
        valeurBoursieres.add(new ValeurBoursiere("3M","3M",new Cours(166.16),Pays.ROYUAME_UNI,TypeValeurs.ACTION));
        valeurBoursieres.add(new ValeurBoursiere("PROCTER&GAMBLE","PROCTERGAMBLE",new Cours(80.22),Pays.SUISSE,TypeValeurs.ACTION));
        valeurBoursieres.add(new ValeurBoursiere("INTEL","INTEL",new Cours(30.23),Pays.USA,TypeValeurs.ACTION));
        valeurBoursieres.add(new ValeurBoursiere("MCDONALD'S","MCDONALDS",new Cours(122.86),Pays.USA,TypeValeurs.ACTION));

        valeurBoursieres.add(new ValeurBoursiere("BOEING CO","BOEING CO",new Cours(127.63),Pays.USA,TypeValeurs.ACTION));
        valeurBoursieres.add(new ValeurBoursiere("COCA-COLA CO","COCA_COLA",new Cours(43.99),Pays.USA,TypeValeurs.ACTION));
        valeurBoursieres.add(new ValeurBoursiere("SOCIETE GENERALE","SOCIETE GENERALE",new Cours(35.92),Pays.FRANCE,TypeValeurs.ACTION));



        return valeurBoursieres;

    }
}

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

import static ch.sebooom.EventBusAdress.INDICES_ALL;
import static ch.sebooom.EventBusAdress.INDICES_RANDOM;

/**
 * Created by seb on 24.05.16.
 */
public class IndicesEBDataGeneratorVerticle extends AbstractValeursBoursieresDatas {


    private static final Logger log = LoggerFactory.getLogger(IndicesEBDataGeneratorVerticle.class);
    private static final String TAG = "[IndicesEBDataGeneratorVerticle] - ";
    private static final String GET = "get";


    @Override
    public void start () {

        initData();

        startEventBusListenning();

        startWebSocketFlow(INDICES_RANDOM);

    }

    void startEventBusListenning() {
        vertx.eventBus()
            .consumer(INDICES_ALL.adress())
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

        valeurBoursieres.add(new ValeurBoursiere("CAC 40","CAC40",new Cours(4325.10), Pays.FRANCE, TypeValeurs.INDICE_BOURSIER));
        valeurBoursieres.add(new ValeurBoursiere("DAX PERFORMANCE-INDEX","DAX_PERFORMANCE_INDEX",new Cours(9842.91),Pays.ALLEMAGNE,TypeValeurs.INDICE_BOURSIER));
        valeurBoursieres.add(new ValeurBoursiere("IBEX 35","IBEX35",new Cours(8714.21),Pays.ESPAGNE,TypeValeurs.INDICE_BOURSIER));
        valeurBoursieres.add(new ValeurBoursiere("FTSE MIB","FTSEMIB",new Cours(17812.93),Pays.ITALIE,TypeValeurs.INDICE_BOURSIER));
        valeurBoursieres.add(new ValeurBoursiere("AEX","AEX",new Cours(1432.55),Pays.PAYS_BAS,TypeValeurs.INDICE_BOURSIER));
        valeurBoursieres.add(new ValeurBoursiere("SMI","SMI",new Cours(7997.55),Pays.SUISSE,TypeValeurs.INDICE_BOURSIER));
        valeurBoursieres.add(new ValeurBoursiere("FTSE 100","FTSE100",new Cours(6136.43),Pays.ROYUAME_UNI,TypeValeurs.INDICE_BOURSIER));
        valeurBoursieres.add(new ValeurBoursiere("RTS Index","RTSINDEX",new Cours(3879.55),Pays.RUSSIE,TypeValeurs.INDICE_BOURSIER));

        valeurBoursieres.add(new ValeurBoursiere("S&P/TSX","SPTSX",new Cours(4879.55),Pays.CANADA,TypeValeurs.INDICE_BOURSIER));
        valeurBoursieres.add(new ValeurBoursiere("DOW JONES INDUSTRIAL AVERAGE","DOWJONES_INDUSTRIAL_AVERAGE",new Cours(17500.55),Pays.USA,TypeValeurs.INDICE_BOURSIER));
        valeurBoursieres.add(new ValeurBoursiere("BUENOS AIRES MERVAL","BUENOS_AIRES_MERVAL",new Cours(12538.14),Pays.ARGENTINE,TypeValeurs.INDICE_BOURSIER));


        valeurBoursieres.add(new ValeurBoursiere("HONG KONG HANG SENG INDICE","HONG_KONG_HANG_SENG_INDICE",new Cours(19809.14),Pays.HONG_KONG,TypeValeurs.INDICE_BOURSIER));
        valeurBoursieres.add(new ValeurBoursiere("SHANGHAI COMPOSITE","SHANGHAI_COMPOSITE",new Cours(2843.65),Pays.CHINE,TypeValeurs.INDICE_BOURSIER));
        valeurBoursieres.add(new ValeurBoursiere("S&P BSE SENSEX","SP_BSE_SENSEX",new Cours(25230),Pays.INDE,TypeValeurs.INDICE_BOURSIER));

        return valeurBoursieres;

    }

}

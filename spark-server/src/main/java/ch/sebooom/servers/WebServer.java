package ch.sebooom.servers;


import bourse.Indices;
import bourse.ValeurBoursiere;
import ch.sebooom.mongodb.MongoService;
import ch.sebooom.rest.RestService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import rx.Observable;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static ch.sebooom.servers.WebServerConfig.DefaultValues.*;
import static ch.sebooom.servers.WebServerPath.*;
import static spark.Spark.*;

/**
 * Classe encapsulant la gestion d'un serveur http basé sur SPARK
 * Paramètres optionnels à passer en argument:
 * -h [host], défaut : localhost
 * -p [port], défaut : 8888
 *
 */
public class WebServer {
    //Objet indicesCache faisant office de cache avec date données
    private static Indices indicesCache = null;
    private static final long VALIDITE_CACHE = 60 * 1000;

    //logger
    public static final Logger log = Logger.getLogger(WebServer.class.getName());
    public static final Gson gson = new GsonBuilder().create();

    //Objet contenant la configuration par défaut du serveur
    WebServerConfig config;


    /**
     * Constuit une instance et la rend prete au démarrage
     * @param args les arguments
     */
    public WebServer(String... args){
        initConfig(args);
    }
    /**
     * Server entry point
     * @param args
     */
	public static void main(String... args) {

        new WebServer(args).start();
	}

    private void start() {
        port(config.getPort());
        staticFileLocation(WebServerConfig.DefaultValues.WS_SERVER_WWW.strValue);
        initRoutes();
    }

    private void initRoutes() {

        get(ROOT.path(), (req, res ) -> {
            Map<String, Object> model = new HashMap<>();

            // The wm files are located under the resources directory
            return new ModelAndView(model, "/public/index.vm");
        }, new VelocityTemplateEngine());

        get(TEST.path(), (req, res) -> "{\"test\":\"ok\"}");

        get(EVENTS.path(), (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("hello", "Velocity World");
            model.put("person", "Foobar");

            // The wm files are located under the resources directory
            return new ModelAndView(model, "/public/events.vm");
        }, new VelocityTemplateEngine());

        get(GPS_CLIENT.path(), (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            // The wm files are located under the resources directory
            return new ModelAndView(model, "/public/gpsApp.vm");
        }, new VelocityTemplateEngine());


        get(INDICES_CLIENT.path(), (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("wsUrl","ws://" +  WS_SERVER_HOST.strValue + ":" + WS_SERVER_PORT.intValue + WS_SERVER_INDICES_PATH.strValue );

            model.put("restUrl",REST_INDICES_PATH.strValue);
            model.put("serverUri", config.getHost() + ":" +config.getPort());

            model.put("typesValeurs",new String[]{"Indices boursiers","Actions","Taux de change"});

            new RestService().findAllIndices().toArray();



            ;
            model.put("indices",new RestService().findAllIndices().toArray());
            // The wm files are located under the resources directory
            return new ModelAndView(model, "/public/stockApp.vm");
        }, new VelocityTemplateEngine());


        get(REST_INDICES.path(), (request,response) -> {


            final StringBuilder jsonReturnString = new StringBuilder();


            // Create our sequence for querying best available data
            Observable<Indices> source = Observable.concat(
                    this.getIndicesCache(),
                    MongoService.getIndiceDao().findAllIndices(),
                    new RestService().findAllIndicesAndUpdateCaches(this)
            )
                    .first(data -> data != null && data.isUpToDate());

            source.subscribe(
                    (indices) -> {
                        System.out.println(indices);
                        jsonReturnString.append(gson.toJson(indices));
                    },
                    (erreur)-> {
                        erreur.printStackTrace();
                        log.severe(erreur.getMessage());
                    },
                    () -> {
                        log.info("Complete!");
                    }
            );
            return jsonReturnString;
        });
    }

    /**
     * Initialisation de l'objet contenant la configuration su serveur
     * @param args les arguments
     */
    private void initConfig(String... args){
        //si config ok on traite
        if(WebServerConfig.checkArgs(args)){
            config = WebServerConfig.extractArgs(args);
            log.info("Arguments passed in the app. Values will be applied:");
            log.info("host : " + config.getHost() + " , port : " + config.getPort());

        }else{
            config = new WebServerConfig();
            log.info("No arguments passed in the app or not valid. Default values will be applied:");
            log.info("host : " + WS_SERVER_HOST.strValue + " , port : " + WS_SERVER_PORT.intValue);

        }

    }



    private  Observable<Indices> getIndicesCache () {

        Observable obs = Observable.create(subscriber -> {
            log.info("Retrieving datas from Application InMemory Cache");

            boolean cacheEmpty = (indicesCache != null && indicesCache.containsData()) ? Boolean.FALSE : Boolean.TRUE;

            log.info("Application InMemory Cache contains " + ((cacheEmpty) ? "no values": indicesCache.getValeurBoursieres().size() + " values. "));

            if(!cacheEmpty){log.info("Application InMemory Cache expire on : " + indicesCache.getExpirationDonnees());};

            log.info("Application InMemory Cache datas are " + ((indicesCache != null && indicesCache.isUpToDate()) ? "upToDate" : "stale"));

            subscriber.onNext(indicesCache);
            subscriber.onCompleted();

        });



        return obs;
    }

    public  static void updateIndicesCaches (Indices restIndices){

        indicesCache = new Indices(VALIDITE_CACHE);

        for(ValeurBoursiere valeurBoursiere : restIndices.getValeurBoursieres()){
            indicesCache.addIndice(valeurBoursiere);
        }

        log.info("Indices Memory Cache is now upToDate");
        log.info(indicesCache.toString());

    }



}

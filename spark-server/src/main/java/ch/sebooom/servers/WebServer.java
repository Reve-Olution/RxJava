package ch.sebooom.servers;


import ch.sebooom.mongodb.MongoService;
import ch.sebooom.rest.RestService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cotation.Indice;
import cotation.Indices;
import rx.Observable;
import spark.ModelAndView;
import spark.template.velocity.VelocityTemplateEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import static ch.sebooom.servers.WebServer.Paths.*;
import static spark.Spark.*;

public class WebServer {
    //Objet indices faisant office de cache avec date données
    private static Indices indicesCache = null;
    private static final long VALIDITE_CACHE = 60 * 1000;

    //logger
    public static final Logger log = Logger.getLogger(WebServer.class.getName());
    public static final Gson gson = new GsonBuilder().create();

    //Server
    Config serverConfig;
    private static final String WEB_FOLDER = "/public";

    enum Paths {
        ROOT("/"),
        TEST("/test"),
        EVENTS("/events"),
        GPS_CLIENT("/gpsSocket"),
        INDICES_CLIENT("/indicesSocket"),
        REST_INDICES("/indices");

        private String path;

        Paths(String path){
            this.path = path;
        }

    }
    /**
     * Server entry point
     * @param args
     */
	public static void main(String... args) {

        WebServer server = new WebServer();

        server.initConfig(args);

        server.start();




	}

    private void start() {
        port(serverConfig.port);
        staticFileLocation(WEB_FOLDER);
        initRoutes();
    }

    private void initRoutes() {

        get(ROOT.path, (req, res ) -> {
            Map<String, Object> model = new HashMap<>();

            // The wm files are located under the resources directory
            return new ModelAndView(model, "/public/index.vm");
        }, new VelocityTemplateEngine());

        get(TEST.path, (req, res) -> "{\"test\":\"ok\"}");

        get(EVENTS.path, (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("hello", "Velocity World");
            model.put("person", "Foobar");

            // The wm files are located under the resources directory
            return new ModelAndView(model, "/public/events.vm");
        }, new VelocityTemplateEngine());

        get(GPS_CLIENT.path, (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            // The wm files are located under the resources directory
            return new ModelAndView(model, "/public/gpsSocketClient.vm");
        }, new VelocityTemplateEngine());

        get(INDICES_CLIENT.path, (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            // The wm files are located under the resources directory
            return new ModelAndView(model, "/public/stockSocketClientRealTime.vm");
        }, new VelocityTemplateEngine());


        get(REST_INDICES.path, (request,response) -> {


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

    private void initConfig(String... args){
        //si pas d'arguments, valeur par défaut
        if(args.length == 0){
            serverConfig = new Config();
            log.info("No arguments passed in the app. Default values will be applied:");
            log.info("server.port: " + serverConfig.port);
        }else{
            serverConfig = extractArgsToConfig(args);
            log.info("Arguments passed in the app.");
            log.info("server.port: " + serverConfig.port);
        }

    }

    private  Config extractArgsToConfig(String[] args) {


            return new Config(Integer.parseInt(args[0]));

    }

    private  Observable<Indices> getIndicesCache () {

        Observable obs = Observable.create(subscriber -> {
            log.info("Retrieving datas from Application InMemory Cache");

            boolean cacheEmpty = (indicesCache != null && indicesCache.containsData()) ? Boolean.FALSE : Boolean.TRUE;

            log.info("Application InMemory Cache contains " + ((cacheEmpty) ? "no values": indicesCache.getIndices().size() + " values. "));

            if(!cacheEmpty){log.info("Application InMemory Cache expire on : " + indicesCache.getExpirationDonnees());};

            log.info("Application InMemory Cache datas are " + ((indicesCache != null && indicesCache.isUpToDate()) ? "upToDate" : "stale"));

            subscriber.onNext(indicesCache);
            subscriber.onCompleted();

        });



        return obs;
    }

    public  static void updateIndicesCaches (Indices restIndices){

        indicesCache = new Indices(VALIDITE_CACHE);

        for(Indice indice : restIndices.getIndices()){
            indicesCache.addIndice(indice);
        }

        log.info("Indices Memory Cache is now upToDate");
        log.info(indicesCache.toString());

    }

    static class Config {
        private final static int DEFAULT_PORT = 9999;
        private int port;


        Config () {
            this.port = DEFAULT_PORT;
        }

        Config (int port){
            this.port = port;
        }

        int port(){
            return port;
        }

    }

}

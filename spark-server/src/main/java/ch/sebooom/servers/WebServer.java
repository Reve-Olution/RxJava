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

import static spark.Spark.*;

public class WebServer {
    //Objet indices faisant office de cache avec date données
    private static Indices indicesCache = null;
    private static final long VALIDITE_CACHE = 60 * 1000;

    //logger
    public static final Logger log = Logger.getLogger(WebServer.class.getName());
    public static Gson gson = new GsonBuilder().create();

	public static void main(String[] args) {
		
		port(9999);
		staticFileLocation("/public");

		get("/hello", (req, res) -> "{\"test\":\"ok\"}");

		get("/", (req, res ) -> {
            Map<String, Object> model = new HashMap<>();

            // The wm files are located under the resources directory
            return new ModelAndView(model, "/public/index.vm");
		}, new VelocityTemplateEngine());

		get("/events", (request, response) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("hello", "Velocity World");
            model.put("person", "Foobar");

            // The wm files are located under the resources directory
            return new ModelAndView(model, "/public/events.vm");
        }, new VelocityTemplateEngine());


		get("/gpsSocket", (request, response) -> {
			Map<String, Object> model = new HashMap<>();
			// The wm files are located under the resources directory
			return new ModelAndView(model, "/public/gpsSocketClient.vm");
		}, new VelocityTemplateEngine());

		get("/stockSocket", (request, response) -> {
			Map<String, Object> model = new HashMap<>();
			// The wm files are located under the resources directory
			return new ModelAndView(model, "/public/stockSocketClientRealTime.vm");
		}, new VelocityTemplateEngine());
		

        get("/indices", (request,response) -> {


            final StringBuilder jsonReturnString = new StringBuilder();


            // Create our sequence for querying best available data
            Observable<Indices> source = Observable.concat(
                    getIndicesCache(),
                    MongoService.getIndiceDao().findAllIndices(),
                    new RestService().findAllIndices()
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

    private static Observable<Indices> getIndicesCache () {

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

    public static void updateIndicesCaches (Indices restIndices){

        indicesCache = new Indices(VALIDITE_CACHE);

        for(Indice indice : restIndices.getIndices()){
            indicesCache.addIndice(indice);
        }

        log.info("Indices Memory Cache is now upToDate");
        log.info(indicesCache.toString());

    }

}

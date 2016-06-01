package ch.sebooom.rest;


import bourse.Indices;
import bourse.ValeurBoursiere;
import ch.sebooom.mongodb.MongoService;
import ch.sebooom.servers.WebServer;
import com.google.gson.*;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by seb on 11.05.16.
 */
public class RestService {

    private Gson gson;
    public static final Logger log = Logger.getLogger(RestService.class.getName());



    public RestService () {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return new Date(jsonElement.getAsJsonPrimitive().getAsLong());
            }
        });
        gson = builder.create();
    }


    public void test () {

    }

    public Observable<Indices> findAllIndicesAndUpdateCaches (WebServer server) {


        Observable obs = getFindAllAsObservable();


        return obs.doOnNext(ids -> {
                    log.info("DO ON NEXT");
                    WebServer.updateIndicesCaches((Indices) ids);
                    MongoService.getIndiceDao().saveIndices((Indices)ids);

                });

    }

    public List<String> findAllIndices () {

        List<String> indicesList = new ArrayList<>();


        getFindAllAsObservable().subscribe(indices -> {

            indices.getValeurBoursieres().forEach(indice -> {
                indicesList.add(indice.getNom());
            });
        });

        return indicesList;
    }

    private Observable<Indices> getFindAllAsObservable() {

        return Observable.create(subscriber -> {

                Indices indices = null;

                IndicesRestService indiceService = new RestAdapter.Builder()
                        .setEndpoint(IndicesRestService.ENDPOINT)
                        .setConverter(new GsonConverter(gson))
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .build()
                        .create(IndicesRestService.class);

                indices = indiceService.getIndices();

                subscriber.onNext(indices);
                subscriber.onCompleted();
            });
    }
}

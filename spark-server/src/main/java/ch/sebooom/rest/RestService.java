package ch.sebooom.rest;


import ch.sebooom.mongodb.MongoService;
import ch.sebooom.servers.WebServer;
import com.google.gson.*;
import cotation.Indices;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by seb on 11.05.16.
 */
public class RestService {

    private GsonBuilder builder = new GsonBuilder();
    private Gson gson;
    public static final Logger log = Logger.getLogger(RestService.class.getName());



    public RestService () {
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                return new Date(jsonElement.getAsJsonPrimitive().getAsLong());
            }
        });
        gson = builder.create();
    }

    public Observable<Indices> findAllIndices () {




        Observable obs = Observable.create(subscriber -> {

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




                return obs.doOnNext(ids -> {
                    log.info("DO ON NEXT");
                    WebServer.updateIndicesCaches((Indices) ids);
                    MongoService.getIndiceDao().saveIndices((Indices)ids);

                });

    }
}

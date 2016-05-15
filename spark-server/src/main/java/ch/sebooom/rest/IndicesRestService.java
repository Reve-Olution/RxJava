package ch.sebooom.rest;


import cotation.Indices;
import retrofit.http.GET;

/**
 * Created by seb on 11.05.16.
 */
public interface IndicesRestService {
    public static final String ENDPOINT = "http://localhost:8888";

    @GET("/api/indices")
    Indices getIndices();
}

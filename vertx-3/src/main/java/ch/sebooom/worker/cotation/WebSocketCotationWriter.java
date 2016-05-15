package ch.sebooom.worker.cotation;

import ch.sebooom.worker.WebSocketWriter;
import cotation.Indice;
import cotation.Indices;
import io.vertx.core.json.Json;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.http.ServerWebSocket;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by seb on 03.05.16.
 */
public class WebSocketCotationWriter implements WebSocketWriter{

    private static final Logger log = LoggerFactory.getLogger(WebSocketCotationWriter.class);
    private ServerWebSocket websocket = null;

    @Override
    public WebSocketWriter socket(ServerWebSocket socket) {
        this.websocket = socket;
        return this;
    }



    @Override
    public void start() {

        List<Indice> courses = Indices.getIndicesAsList();

        ExecutorService executorService = Executors.newFixedThreadPool(courses.size());

        for(Indice indice : courses){



            executorService.execute(() -> {

                boolean running = Boolean.TRUE;

                while(running){

                    try {
                        Thread.sleep(getRandomMillisForThreadSleep(200,3000));
                        websocket.writeFinalTextFrame(Json.encode(indice));
                        indice.defineCours();

                    } catch (InterruptedException | IllegalStateException e) {

                        if(e instanceof IllegalStateException){
                            running = Boolean.FALSE;
                        }else{
                            log.error(e.getMessage());
                        }
                    }



                }



            });
        }



    }



    protected int getRandomMillisForThreadSleep(int min, int max){

        int threadSleepMillis = 0;

        while(threadSleepMillis < min || threadSleepMillis > max){
            threadSleepMillis = (int) (Math.random() * max);
        }

        return threadSleepMillis;
    }







}

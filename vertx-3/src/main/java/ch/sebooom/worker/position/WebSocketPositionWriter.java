package ch.sebooom.worker.position;

import ch.sebooom.worker.WebSocketWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.http.ServerWebSocket;
import position.Position;
import position.RandomPositionGenerator;
import position.TrackingObject;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by seb on 01.05.16.
 */
public class WebSocketPositionWriter implements WebSocketWriter{


    private static final Logger log = LoggerFactory.getLogger(WebSocketPositionWriter.class);
    private ServerWebSocket websocket = null;
    private List<TrackingObject> trackingObjects = null;
    private static Gson gson = new GsonBuilder().create();

    public WebSocketPositionWriter() {
        this.trackingObjects = TrackingObject.generateRandomObjectsList();
    }



    public void start () {

        if(websocket == null){
            throw new IllegalArgumentException("WebSocket must be set with method socket first!");
        }

        log.info("[" +Thread.currentThread().getName()
                +"] WebSocketWriter starting");

        int numberOfThreads = trackingObjects.size();
        log.info(numberOfThreads + " threads");
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);


        for(TrackingObject trackingObject : trackingObjects) {

            executor.execute(() ->  {

                RandomPositionGenerator positionGenerator = new RandomPositionGenerator(Position.generateSample(trackingObject));

                log.info("Execute...");


                    boolean running = Boolean.TRUE;

                    while(running){

                        try {
                            Thread.sleep(getRandomMillisForThreadSleep(200, 2000));
                            String message = gson.toJson(positionGenerator.next());
                            websocket.writeFinalTextFrame(message);
                            log.info("Writing...[" + message + "]");
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

    @Override
    public WebSocketPositionWriter socket(ServerWebSocket socket) {
        this.websocket = socket;
        return this;
    }




    protected int getRandomMillisForThreadSleep(int min, int max){

        int threadSleepMillis = 0;

        while(threadSleepMillis < min || threadSleepMillis > max){
            threadSleepMillis = (int) (Math.random() * max);
        }

        return threadSleepMillis;
    }


}

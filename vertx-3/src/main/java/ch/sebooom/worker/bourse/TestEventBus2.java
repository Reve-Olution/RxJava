package ch.sebooom.worker.bourse;

import ch.sebooom.EventBusAdress;
import io.vertx.core.AbstractVerticle;

/**
 * Created by seb on 24.05.16.
 */
public class TestEventBus2 extends AbstractVerticle{

        @Override
        public void start() throws Exception {

            vertx.eventBus().consumer(EventBusAdress.INDICES_RANDOM.adress(),msg -> {
                System.out.println("Message TESTEB2: " + msg);
            });


            vertx.eventBus().consumer("TEST",handler -> {
                System.out.println("OK MESSAGE: "  + handler.body());
            });

            vertx.eventBus().publish("TEST","Test ok testeb2");
        }


}

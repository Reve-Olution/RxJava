package ch.sebooom.worker.bourse;

import ch.sebooom.EventBusAdress;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.VertxOptions;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Observable;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Created by seb on 24.05.16.
 */
@RunWith(VertxUnitRunner.class)
public class IndicesEBDataGeneratorVerticleTest {

    //Vertx vertx;

    @Before
    public void setUp (TestContext context) throws InterruptedException {
//options 'dinstances vertx
        VertxOptions voptions = new VertxOptions();
        voptions.setClusterHost("127.0.0.1");
        voptions.setHAEnabled(true);

        Async async = context.async();

        //déploiement du cluster
        Vertx.clusteredVertx(voptions, result -> {

            if(result.succeeded()){
                Vertx vertx = result.result();
                context.put("vertx-cluster",vertx);
                Observable<String> deployObs = vertx.deployVerticleObservable(IndicesEBDataGeneratorVerticle.class.getName(),new DeploymentOptions());

                deployObs.subscribe(verticle -> {
                    async.complete();
                    System.out.println("Verticle deployed");

                });




            }


        });



    }

    @Test
    public void testEventBusData (TestContext context) throws InterruptedException {

        //Vertx vertx = Vertx.vertx();
        Async async2 = context.async();

        ((Vertx)context.get("vertx-cluster")).eventBus()
                .consumer(EventBusAdress.INDICES_RANDOM.adress())
                .toObservable()
                .limit(5)
                .subscribe(message -> {
                    System.out.println("Message from EB: " + message);


                },error -> {
                    System.out.println(error);
                    fail();
                },() -> {
                    System.out.println("Complete");
                    assertTrue(true);
                    async2.complete();
                });




    }

}
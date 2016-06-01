package ch.sebooom;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Observable;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * Created by seb on 22.05.16.
 */
@RunWith(VertxUnitRunner.class)
public class WSIndicesServerTest {

    private Vertx vertx = Vertx.vertx();

    @Test
    public void testHttpPortParams (TestContext context) throws InterruptedException {
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("htt.port", 9898));

        Observable<String> serverObs = vertx.deployVerticleObservable(WSIndicesServer.class.getName(),options);

        System.out.println(serverObs);

        serverObs.subscribe(
                server -> {
                    assertNotNull(serverObs);
                },
                error -> {
                    assertTrue(error instanceof IllegalArgumentException);
                },
                () -> {
                    fail();
                }
        );

        Thread.sleep(3000);

        options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", 9898));
        Observable<String>serverObs2 = vertx.deployVerticleObservable(WSIndicesServer.class.getName(),options);



        serverObs.subscribe(
                server -> {
                    assertNotNull(serverObs2);
                },
                error -> {
                    fail();
                },
                () -> {
                    assertTrue(true);
                }
        );

        Thread.sleep(3000);



    }
}

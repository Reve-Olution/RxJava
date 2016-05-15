import ch.sebooom.servers.WebServer;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.WebSocket;
import io.vertx.rxjava.core.http.WebSocketStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by seb on 01.05.16.
 */
@RunWith(VertxUnitRunner.class)
public class WebServerTest {

    private Vertx vertx;
    private final static List<Integer> serverPortsList = new ArrayList<>(2);
    private static final Logger log = LoggerFactory.getLogger(WebServerTest.class);

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        initTestsParameters();

        deployWebServers(context);

    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testAllInstances (TestContext context) {

        for(Integer port : serverPortsList) {
            final Async async = context.async();

            vertx.createHttpClient().getNow(port, "localhost", "/test",
                    response -> {
                        response.handler(body -> {
                            context.assertTrue(body.toString().contains(WebServer.H1_TEST_MESSAGE));
                            async.complete();
                        });
                    });
        }
    }

    @Test
    public void testWebSocketForAllInstance (TestContext context) throws InterruptedException {
        HttpClient client = vertx.createHttpClient(new HttpClientOptions());

        for(Integer port : serverPortsList){
            WebSocketStream stream = client.websocketStream(port, "localhost", "/ws");

            stream.toObservable()
                    .flatMap(WebSocket::toObservable)
                    .skip(2)
                    .subscribe(
                            ws -> {
                                String message = ws.getString(0,ws.length());
                                System.out.println(message);
                                context.assertTrue(message.contains("lat"));
                                context.assertTrue(message.contains("lng"));
                                context.assertTrue(message.contains("datePosition"));
                                context.assertTrue(message.contains("trackingObject"));
                            },
                            error -> {log.info("dsfs");}
                    );


        }



        Thread.sleep(5000);


    }


    private void deployWebServers (TestContext context) {
        //Default port 8989
        vertx.deployVerticle(WebServer.class.getName(),
                context.asyncAssertSuccess());

        //Autres instances de la liste des ports
        for(Integer port: serverPortsList){
            DeploymentOptions options = new DeploymentOptions()
                    .setConfig(new JsonObject().put("http.port", port)
                    );
            vertx.deployVerticle(WebServer.class.getName(),options,
                    context.asyncAssertSuccess());
        }

    }

    private void initTestsParameters () {
        serverPortsList.add(9998);
        serverPortsList.add(9999);

    }
}


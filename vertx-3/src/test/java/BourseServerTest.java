import ch.sebooom.servers.Paths;
import ch.sebooom.servers.BourseServer;
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
import java.util.Arrays;
import java.util.List;

/**
 * Created by seb on 01.05.16.
 */
@RunWith(VertxUnitRunner.class)
public class BourseServerTest {

    private Vertx vertx;
    private final static List<Integer> serverPortsList = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(BourseServerTest.class);

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        initTestsParameters();

        deployWebServers(context);

    }

    @After
    public void tearDown(TestContext context) {

        vertx.close(context.asyncAssertSuccess());
        log.info("[webserver.close]" + " closing instances");
    }

    @Test
    public void testAllInstances (TestContext context) {

        for(Integer port : serverPortsList) {
            final Async async = context.async();

            vertx.createHttpClient().getNow(port, "localhost", "/test",
                    response -> {
                        response.handler(body -> {
                            context.assertTrue(body.toString().contains(BourseServer.H1_TEST_MESSAGE));
                            async.complete();
                        });
                    });
        }
    }

    @Test
    public void testAllowedRoutes (TestContext context) throws InterruptedException {

        HttpClient client = vertx.createHttpClient(new HttpClientOptions());

        //creation d'un stream sur chaque port
        serverPortsList.stream()

                //conversion des ports en flux PathWithPort (pour garder l'infos port)
                .flatMap(


                     port -> {


                        List<PathsWithPort> pathsByPort = new ArrayList<PathsWithPort>();
                        Arrays.asList(Paths.values()).forEach(route -> {
                            pathsByPort.add(new PathsWithPort(port,route));
                        });

                        return pathsByPort.stream();
                    }
                ).forEach(websocketRoute -> {

                        log.info("[test] " + "Create new WebSocketClient : " + websocketRoute.port+":"+websocketRoute.paths );
                        WebSocketStream stream = client.websocketStream(websocketRoute.port, "localhost", websocketRoute.paths.path());
                        log.info("[test] " + "WebSocketClientStream : " + stream.toString()  );


            stream.toObservable()
                                .flatMap(WebSocket::toObservable)
                                .subscribe(
                                        ws -> {

                                            String message = ws.getString(0,ws.length());
                                            log.info("[test] [ " + websocketRoute.port+":"+websocketRoute.paths.path() +"]" + message);
                                            context.assertNotNull(message);
                                            context.assertFalse(message.isEmpty());

                                        },
                                        error -> {error.printStackTrace();}
                                );
                    });

        Thread.sleep(5000);
    }

    @Test
    public void testWebSocketForAllInstance (TestContext context) throws InterruptedException {
        HttpClient client = vertx.createHttpClient(new HttpClientOptions());

        for(Integer port : serverPortsList){
            WebSocketStream stream = client.websocketStream(port, "localhost", "/ws");

            stream.toObservable()
                    .flatMap(WebSocket::toObservable)
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



        Thread.sleep(3000);


    }


    private void deployWebServers (TestContext context) {
        //Default port 8989
        vertx.deployVerticle(BourseServer.class.getName(),
                context.asyncAssertSuccess());

        //Autres instances de la liste des ports
        for(Integer port: serverPortsList){
            DeploymentOptions options = new DeploymentOptions()
                    .setConfig(new JsonObject().put("http.port", port)
                    );
            vertx.deployVerticle(BourseServer.class.getName(),options,
                    context.asyncAssertSuccess());
        }

    }

    private void initTestsParameters () {
        serverPortsList.add(BourseServer.DEFAULT_PORT);
        serverPortsList.add(9998);
        serverPortsList.add(9999);

    }

    class PathsWithPort {
        int port;
        Paths paths;

        PathsWithPort(int port, Paths paths){
            this.port = port;
            this.paths = paths;
        }
    }
}


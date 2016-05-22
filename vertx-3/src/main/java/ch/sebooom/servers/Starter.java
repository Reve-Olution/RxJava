package ch.sebooom.servers;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;


/**
 * Classe permettant de piloter les verticles du projet
 *
 */
public class Starter {

    private static final int DEFAULT_PORT = 8888;
    private static final Vertx vertx = Vertx.vertx();
    private static final Logger log = LoggerFactory.getLogger(Starter.class);
    private static List<Integer> ports = null;

    public static void main(String[] args) {


        ports = new ArrayList<Integer>();

        List<Observable<String>> instancesToLaunch = null;

        for(String port : args){
            ports.add(Integer.parseInt(port));
        }



        instancesToLaunch = getInstancesObservable(ports);


        Observable.merge(instancesToLaunch)
            .subscribe(
                next -> {
                    log.info("Starting instance id: " + next);

                },
                error -> {
                    System.out.println(error);
                },
                () -> {
                    splashConsole();
                }
        );


    }

    private static void splashConsole () {

        System.out.println(" __     __        _            _____    ___  _                             _     _     ");
        System.out.println(" \\ \\   / /__ _ __| |_  __  __ |___ /   / _ \\| |__  ___  ___ _ ____   ____ _| |__ | | ___");
        System.out.println("  \\ \\ / / _ \\ '__| __| \\ \\/ /   |_ \\  | | | | '_ \\/ __|/ _ \\ '__\\ \\ / / _` | '_ \\| |/ _ \\");
        System.out.println("   \\ V /  __/ |  | |_ _ >  <   ___) | | |_| | |_) \\__ \\  __/ |   \\ V / (_| | |_) | |  __/");
        System.out.println("    \\_/ \\___|_|   \\__(_)_/\\_\\ |____/   \\___/|_.__/|___/\\___|_|    \\_/ \\__,_|_.__/|_|\\___|");
        System.out.println("  _____ _____ _____ _____ _____ _____ _____ _____ _____ _____ _____ _____ _____ _____ _____");
        System.out.println(" |_____|_____|_____|_____|_____|_____|_____|_____|_____|_____|_____|_____|_____|_____|_____|");
        System.out.println(" | |__   __ _ ___  ___  __| | \\ \\      / /__| |__/ ___|  ___   ___| | _____| |_");
        System.out.println(" | '_ \\ / _` / __|/ _ \\/ _` |  \\ \\ /\\ / / _ \\ '_ \\___ \\ / _ \\ / __| |/ / _ \\ __|");
        System.out.println(" | |_) | (_| \\__ \\  __/ (_| |   \\ V  V /  __/ |_) |__) | (_) | (__|   <  __/ |_");
        System.out.println(" |_.__/ \\__,_|___/\\___|\\__,_|    \\_/\\_/ \\___|_.__/____/ \\___/ \\___|_|\\_\\___|\\__|");
        System.out.println("  _____ _____ _____ _____ _____ _____ _____ _____ _____ _____ _____ _____ _____ _____ _____");
        System.out.println(" |_____|_____|_____|_____|_____|_____|_____|_____|_____|_____|_____|_____|_____|_____|_____|");
        System.out.println("**********************************************************************************************");
        System.out.println(" " + ports.size() +" instances launched: ");

        for(Integer port : ports){
            System.out.println(" [" + port + "]");

        }
    }



    private static List<Observable<String>> getInstancesObservable (List<Integer> ports) {
        List<Observable<String>> servers = new ArrayList<>();

        for(Integer port : ports){

            DeploymentOptions options = new DeploymentOptions();
            options.setConfig(new JsonObject().put("http.port",port));
            servers.add(vertx.deployVerticleObservable(WebServer.class.getName(),options));

        }

        return servers;

    }
}

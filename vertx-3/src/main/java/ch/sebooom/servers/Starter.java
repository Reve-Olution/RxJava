package ch.sebooom.servers;

import io.vertx.core.DeploymentOptions;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;


/**
 * Created by seb on 01.05.16.
 */
public class Starter {

    private static final int DEFAULT_PORT = 8888;
    private static final Vertx vertx = Vertx.vertx();
    private static final Logger log = LoggerFactory.getLogger(Starter.class);
    private static List<Integer> ports = null;

    public static void main(String[] args) {


        ports = new ArrayList<Integer>();

        Observable<String> serverLaunchObservable = null;

        if(args.length == 0){
            ports.add(DEFAULT_PORT);
            serverLaunchObservable = uniqueInstanceLauncher(DEFAULT_PORT);
        }else{
            if(args.length == 1){
                ports.add(Integer.parseInt(args[0]));
                serverLaunchObservable = uniqueInstanceLauncher(ports.get(0));
            }else{

                Stream.of(args)
                        .mapToInt(Integer::parseInt)
                        .forEach(ports::add);

                serverLaunchObservable = multipleInstancesLauncher(ports);

            }
        }

        serverLaunchObservable.subscribe(
                next -> {
                    System.out.println(next);

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

        System.out.println();
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

    private static Observable<String> uniqueInstanceLauncher (Integer port) {
        DeploymentOptions options = new DeploymentOptions();
        options.setConfig(new JsonObject().put("http.port",port));
        return vertx.deployVerticleObservable(WebServer.class.getName(),options);

    }

    private static Observable<String> multipleInstancesLauncher (List<Integer> ports) {
        DeploymentOptions options = new DeploymentOptions();

        for(Integer port : ports){

            return uniqueInstanceLauncher(port);

        }

        throw new IllegalArgumentException("Multiple intances failed, no port defined");

    }
}

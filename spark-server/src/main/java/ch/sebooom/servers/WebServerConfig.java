package ch.sebooom.servers;

import java.util.Arrays;
import java.util.List;

/**
 * Created by seb on 20.05.16.
 */
public class WebServerConfig {


    public WebServerConfig(String host, int port) {
        this.port = port;
        this.host = host;
    }

    public static boolean checkArgs(String... arguments) {

        for(String arg : arguments){
            if(arg.startsWith("-") && !isArgsAllowed(arg)){
                return false;
            }
        }

        return arguments.length % 2 == 0 && arguments.length != 0;
    }

    public String getHost() {
        return host;
    }

    public enum DefaultValues{
        WS_SERVER_WWW("/public"),
        REST_INDICES_PATH("/api/indices"),
        WS_SERVER_PORT(8888),
        WS_SERVER_HOST("localhost"),
        WS_SERVER_INDICES_PATH("/"),
        WS_SERVER_GPS_PATH("/");

        public String strValue = null;
        public Integer intValue = null;

        DefaultValues(int intValue){
            this.intValue = intValue;
        }

        DefaultValues(String strValue){
            this.strValue = strValue;
        }


    };

    private final static List<String> argsList = Arrays.asList(new String[]{"-h","-p"});


    private int port;
    private String host;




    WebServerConfig () {

        this(DefaultValues.WS_SERVER_PORT.intValue);
    }

    WebServerConfig (int port){

        this.port = port;
        this.host = DefaultValues.WS_SERVER_HOST.strValue;

    }


    public int getPort(){

        return port;
    }

    public static boolean isArgsAllowed (String args){
        return argsList.contains(args);
    }



    public static WebServerConfig extractArgs(String[] args) {

        List<String> firstArgs = Arrays.asList(args).subList(0,2);
        WebServerConfig toReturn;

        if(firstArgs.get(0).equals("-h")){
            String host = firstArgs.get(1);
            int port = (args.length == 4) ? Integer.parseInt(args[3]) : DefaultValues.WS_SERVER_PORT.intValue;
            toReturn = new WebServerConfig(host,port);
        }else{
            int port = Integer.parseInt(firstArgs.get(1));
            String host = (args.length == 4) ? args[3] : DefaultValues.WS_SERVER_HOST.strValue;
            toReturn =  new WebServerConfig(host,port);

        }

        return toReturn;
    }
}

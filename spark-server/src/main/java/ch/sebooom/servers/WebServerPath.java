package ch.sebooom.servers;

/**
 * Created by seb on 20.05.16.
 */
public enum WebServerPath {

    ROOT("/"),
    TEST("/test"),
    EVENTS("/events"),
    GPS_CLIENT("/gpsSocket"),
    INDICES_CLIENT("/indicesSocket"),
    REST_INDICES("/indicesCache");

    private String path;

    WebServerPath(String path){
        this.path = path;
    }


    public String path () {
        return this.path;
    }
}

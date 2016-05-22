package ch.sebooom.servers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by seb on 19.05.16.
 */
public enum Paths {

    GPS_WEBSOCKET("/ws/gps",PathType.WEBSOCKET),
    COTATION_WEBSOCKET("/ws/cot", PathType.WEBSOCKET),
    API_INDICES("/api/indices", PathType.REST), TEST("/test",PathType.REST );

    private final String path;
    private final PathType type;

    Paths(String path, PathType type){

        this.path = path;
        this.type = type;
    }

    static List<Paths> pathsForType (PathType type) {

        return Stream.of(Paths.values()).filter(path -> {
            return path.type .equals(type);
        }).collect(Collectors.toList());
    }

    static boolean isPathForType(String path, PathType type){

        for(Paths p : Paths.values()){
            if(p.path.equals(path) && p.type.equals(type)){
                return true;
            }
        }

        return false;
    }

    public String path() {
        return path;
    }

    public PathType type () { return type; }
}

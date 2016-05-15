package ch.sebooom.worker;

import io.vertx.rxjava.core.http.ServerWebSocket;

/**
 * Created by seb on 03.05.16.
 */
public interface WebSocketWriter  {

    public void start ();

    public WebSocketWriter socket (ServerWebSocket socket);


}

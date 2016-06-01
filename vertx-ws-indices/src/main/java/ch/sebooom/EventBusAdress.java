package ch.sebooom;

/**
 * Created by seb on 23.05.16.
 */
public enum EventBusAdress {

    INDICES_ALL("using to send indicie state on demand","indices.all"),
    INDICES_RANDOM("using to send random indice","indices.random");

    private String desc;
    private String adress;

    EventBusAdress (String desc, String adress){
        this.adress = adress;
        this.desc = desc;
    }

    public String desc () {
        return desc;
    }

    public String adress () {
        return adress;
    }

}

package ch.sebooom;

/**
 * Created by seb on 23.05.16.
 */
public enum EventBusAdress {

    INDICES_ALL("using to send indices state on demand","bourse.indices.all"),
    INDICES_RANDOM("using to send random indice","bourse.indices.random"),
    ACTIONS_RANDOM("usgin to send random action","bourse.actions.radom"),
    ACTIONS_ALL("usgin to send actions state on demande","bourse.actions.radom");

    private String desc;
    private String adress;

    EventBusAdress(String desc, String adress){
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

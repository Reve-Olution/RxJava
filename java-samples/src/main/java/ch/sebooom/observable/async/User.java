package ch.sebooom.observable.async;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by seb on 18.05.16.
 */
public class User {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String toString() {

        return gson.toJson(this);

    }

    public String login() {
        return login;
    }

    public int age() {
        return age;
    }

    enum Sexe{
      MASCULIN, FEMININ
    };

    private String login;
    private int age;

    public User(String login, String pass, int age, Sexe sexe, boolean isActive) {
        this.login = login;
        String pass1 = pass;
        this.age = age;
        Sexe sexe1 = sexe;
        boolean isActive1 = isActive;
    }
}

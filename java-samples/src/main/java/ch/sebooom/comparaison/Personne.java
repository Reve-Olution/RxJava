package ch.sebooom.comparaison;

/**
 * Created by seb on 30.05.16.
 */
public class Personne {

    private String nom;
    private String prenom;
    private Sexe sexe;
    private int age;

    public enum Sexe {
        HOMME,FEMME;
    };

    private Personne (String nom, String prenom, Sexe sexe, int age) {
        this.nom = nom;
        this.prenom = prenom;
        this.sexe = sexe;
        this.age = age;
    }

    public static Personne getHomme (String nom, String prenom, int age) {

        return new Personne(nom, prenom, Sexe.HOMME, age);
    }

    public static Personne getFemme (String nom, String prenom, int age) {

        return new Personne(nom, prenom, Sexe.FEMME, age);
    }

    public Sexe sexe () {
        return sexe;
    }

    public String nom () {
        return nom;
    }

    public String prenom () {
        return prenom;
    }

    public int age () {
        return age;
    }


}

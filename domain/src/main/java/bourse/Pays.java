package bourse;

import static bourse.Continent.*;

/**
 * Created by seb on 23.05.16.
 */
public enum Pays {


    FRANCE("France",EUROPE),
    ALLEMAGNE("Allemagne",EUROPE),
    ESPAGNE("Espagne",EUROPE),
    ITALIE("Italie",EUROPE),
    PAYS_BAS("Pays-Bas",EUROPE),
    SUISSE("Suisse",EUROPE),
    RUSSIE("Russie",EUROPE),
    ROYUAME_UNI("Royaume-Uni",EUROPE),
    CANADA("Canada",AMERIQUE ),
    USA("Etats-Unis",AMERIQUE ),
    ARGENTINE("Argentine",AMERIQUE ), HONG_KONG("Hong-Kong",ASIE ), CHINE("Chine",ASIE ), INDE("Inde",ASIE );

    private final String nom;
    private final Continent continent;

    Pays(String nom, Continent continent){
        this.nom = nom;
        this.continent = continent;
    }

    public String nom () {
        return nom;
    }

    public Continent continent () {
        return continent;
    }
}

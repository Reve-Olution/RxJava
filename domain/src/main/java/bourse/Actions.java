package bourse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by seb on 24.05.16.
 */
public class Actions {

    private Date expirationDonnees;
    private List<ValeurBoursiere> valeurBoursieres = new ArrayList<>();
    private boolean neverExpire = Boolean.FALSE;
    private boolean containsData = Boolean.FALSE;

    public boolean isNeverExpire() {
        return neverExpire;
    }

    public void setNeverExpire(boolean neverExpire) {
        this.neverExpire = neverExpire;
    }

    public boolean isUpToDate () {

        System.out.println("neverExpire;" + neverExpire);
        System.out.println("now:" +System.currentTimeMillis() + ", exp:" + expirationDonnees.getTime() +", total:" + (System.currentTimeMillis() - expirationDonnees.getTime()));

        if(neverExpire){
            System.out.println("now:" +System.currentTimeMillis() + ", exp:" + expirationDonnees.getTime() +", total:" + (System.currentTimeMillis() - expirationDonnees.getTime()));

            return Boolean.TRUE;
        }
        return (System.currentTimeMillis() - expirationDonnees.getTime()) < 0;
    }

    public Actions(long validiteDonees){
        this.expirationDonnees = new Date(new Date().getTime() + validiteDonees);
    }

    public void defineValidite (long validiteDonnees) {
        this.expirationDonnees = new Date(new Date().getTime() + validiteDonnees);
    }
    public Actions () {
        this(0);
        this.neverExpire = Boolean.TRUE;
    }
    public Date getExpirationDonnees() {
        return expirationDonnees;
    }

    public List<ValeurBoursiere> getValeurBoursieres() {
        return Collections.unmodifiableList(valeurBoursieres);
    }

    public void addIndice (ValeurBoursiere valeurBoursiere) {
        this.valeurBoursieres.add(valeurBoursiere);
        containsData = Boolean.TRUE;
    }

    public void setValeurBoursieres(List<ValeurBoursiere> valeurBoursieres) {
        for(ValeurBoursiere valeurBoursiere : valeurBoursieres){
            addIndice(valeurBoursiere);
        }
    }

    public boolean containsData () {
        return this.containsData;
    }
    /************************** METHODEs STATIQUES UTILITAIRES **************************/

    /**
     * Retourne une liste d'indices servant d'exemples de données-
     * @return une liste d'instance Indice
     */
    public static List<ValeurBoursiere> getIndicesAsList () {

        List<ValeurBoursiere> valeurBoursieres = new ArrayList<>();

        valeurBoursieres.add(new ValeurBoursiere("DU PONT NEMOURS&CO","DU PONT NEMOURSCO",new Cours(67.01),Pays.FRANCE,TypeValeurs.ACTION));
        valeurBoursieres.add(new ValeurBoursiere("APPLE","APPLE",new Cours(96.42),Pays.USA,TypeValeurs.ACTION));
        valeurBoursieres.add(new ValeurBoursiere("GOLDMAN SACHS GROUP","GOLDMAN SACHS GROUP",new Cours(155.58),Pays.USA,TypeValeurs.ACTION));
        valeurBoursieres.add(new ValeurBoursiere("CATERPILLAR","CATERPILLAR",new Cours(70.41),Pays.ALLEMAGNE,TypeValeurs.ACTION));
        valeurBoursieres.add(new ValeurBoursiere("3M","3M",new Cours(166.16),Pays.ROYUAME_UNI,TypeValeurs.ACTION));
        valeurBoursieres.add(new ValeurBoursiere("PROCTER&GAMBLE","PROCTERGAMBLE",new Cours(80.22),Pays.SUISSE,TypeValeurs.ACTION));
        valeurBoursieres.add(new ValeurBoursiere("INTEL","INTEL",new Cours(30.23),Pays.USA,TypeValeurs.ACTION));
        valeurBoursieres.add(new ValeurBoursiere("MCDONALD'S","MCDONALDS",new Cours(122.86),Pays.USA,TypeValeurs.ACTION));

        valeurBoursieres.add(new ValeurBoursiere("BOEING CO","BOEING CO",new Cours(127.63),Pays.USA,TypeValeurs.ACTION));
        valeurBoursieres.add(new ValeurBoursiere("COCA-COLA CO","COCA_COLA",new Cours(43.99),Pays.USA,TypeValeurs.ACTION));
        valeurBoursieres.add(new ValeurBoursiere("SOCIETE GENERALE","SOCIETE GENERALE",new Cours(35.92),Pays.FRANCE,TypeValeurs.ACTION));



        return valeurBoursieres;

    }

    /**
     * Retourne un objet contenant une liste des indices avec des valeurs aléatoires
     * @return ue instance de Indices
     */
    public static Indices getIndicesAleatoire () {

        Indices indices = new Indices();

        for(ValeurBoursiere valeurBoursiere : Indices.getIndicesAsList()){

            valeurBoursiere.nextAleatoire();
            indices.addIndice(valeurBoursiere);

        }

        return indices;

    }
}

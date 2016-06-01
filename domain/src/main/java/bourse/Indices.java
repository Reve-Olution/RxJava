package bourse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Objet manipulant une liste de cours ainsi que la date d'expiration de l'ensemble des données
 */
public class Indices {

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

    public Indices(long validiteDonees){
        this.expirationDonnees = new Date(new Date().getTime() + validiteDonees);
    }

    public void defineValidite (long validiteDonnees) {
        this.expirationDonnees = new Date(new Date().getTime() + validiteDonnees);
    }
    public Indices () {
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
		
		valeurBoursieres.add(new ValeurBoursiere("CAC 40","CAC40",new Cours(4325.10),Pays.FRANCE,TypeValeurs.INDICE_BOURSIER));
		valeurBoursieres.add(new ValeurBoursiere("DAX PERFORMANCE-INDEX","DAX_PERFORMANCE_INDEX",new Cours(9842.91),Pays.ALLEMAGNE,TypeValeurs.INDICE_BOURSIER));
		valeurBoursieres.add(new ValeurBoursiere("IBEX 35","IBEX35",new Cours(8714.21),Pays.ESPAGNE,TypeValeurs.INDICE_BOURSIER));
		valeurBoursieres.add(new ValeurBoursiere("FTSE MIB","FTSEMIB",new Cours(17812.93),Pays.ITALIE,TypeValeurs.INDICE_BOURSIER));
		valeurBoursieres.add(new ValeurBoursiere("AEX","AEX",new Cours(432.55),Pays.PAYS_BAS,TypeValeurs.INDICE_BOURSIER));
        valeurBoursieres.add(new ValeurBoursiere("SMI","SMI",new Cours(7997.55),Pays.SUISSE,TypeValeurs.INDICE_BOURSIER));
        valeurBoursieres.add(new ValeurBoursiere("FTSE 100","FTSE100",new Cours(6136.43),Pays.ROYUAME_UNI,TypeValeurs.INDICE_BOURSIER));
        valeurBoursieres.add(new ValeurBoursiere("RTS Index","RTSINDEX",new Cours(879.55),Pays.RUSSIE,TypeValeurs.INDICE_BOURSIER));

        valeurBoursieres.add(new ValeurBoursiere("S&P/TSX","SPTSX",new Cours(879.55),Pays.CANADA,TypeValeurs.INDICE_BOURSIER));
        valeurBoursieres.add(new ValeurBoursiere("DOW JONES INDUSTRIAL AVERAGE","DOWJONES_INDUSTRIAL_AVERAGE",new Cours(17500.55),Pays.USA,TypeValeurs.INDICE_BOURSIER));
        valeurBoursieres.add(new ValeurBoursiere("BUENOS AIRES MERVAL","BUENOS_AIRES_MERVAL",new Cours(12538.14),Pays.ARGENTINE,TypeValeurs.INDICE_BOURSIER));


        valeurBoursieres.add(new ValeurBoursiere("HONG KONG HANG SENG INDICE","HONG_KONG_HANG_SENG_INDICE",new Cours(19809.14),Pays.HONG_KONG,TypeValeurs.INDICE_BOURSIER));
        valeurBoursieres.add(new ValeurBoursiere("SHANGHAI COMPOSITE","SHANGHAI_COMPOSITE",new Cours(2843.65),Pays.CHINE,TypeValeurs.INDICE_BOURSIER));
        valeurBoursieres.add(new ValeurBoursiere("S&P BSE SENSEX","SP_BSE_SENSEX",new Cours(25230),Pays.INDE,TypeValeurs.INDICE_BOURSIER));

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

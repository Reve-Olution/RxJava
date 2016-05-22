package cotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Objet manipulant une liste de cours ainsi que la date d'expiration de l'ensemble des données
 */
public class Indices {

    private Date expirationDonnees;
    private List<Indice> indices = new ArrayList<>();
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

    public List<Indice> getIndices() {
        return Collections.unmodifiableList(indices);
    }

    public void addIndice (Indice indice) {
        this.indices.add(indice);
        containsData = Boolean.TRUE;
    }

    public void setIndices (List<Indice> indices) {
        for(Indice indice : indices){
            addIndice(indice);
        }
    }

    public boolean containsData () {
        return this.containsData;
    }
    /************************** METHODEs STATIQUES UTILITAIRES **************************/

    /**
     * Retourne une liste d'indices
     * @return une liste d'instance Indice
     */
    public static List<Indice> getIndicesAsList () {
		
		List<Indice> indices = new ArrayList<>();
		
		indices.add(new Indice("CAC40",new Cours(4371.88)));
		indices.add(new Indice("CAC PME",new Cours(1098.91)));
		indices.add(new Indice("DOW JONES INDUSTRY",new Cours(17891.21)));
		indices.add(new Indice("ESTX50",new Cours(2977.93)));
		indices.add(new Indice("NASDAQ",new Cours(4817.55)));
		
		return indices;
		
	}

    /**
     * Retourne un objet contenant une liste des indices avec des valeurs aléatoires
     * @return ue instance de Indices
     */
	public static Indices getIndicesAleatoire () {

		Indices indices = new Indices();

		for(Indice indice : Indices.getIndicesAsList()){

			indice.defineCours();
			indices.addIndice(indice);

		}

		return indices;

	}

}

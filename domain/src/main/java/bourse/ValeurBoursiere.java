package bourse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;


/**
 * Classe modélisant in un indice boursier et son cour
 *
 */
public class ValeurBoursiere {

	private String identifiant;
	private Cours cours;
	private String nom;
	private Pays pays;
	private BigDecimal variationCoursPourCent = BigDecimal.ZERO.setScale(scale,roundingMode);
	private TypeValeurs typeValeur;

	public  static final int scale = 2;
	public static final RoundingMode roundingMode = RoundingMode.HALF_UP;


	public TypeValeurs getTypeValeur() {
		return typeValeur;
	}

	public ValeurBoursiere(String nom, String identifiant, Cours cours, Pays pays, TypeValeurs type) {
		this.identifiant = identifiant;
		this.cours = cours;
		this.nom = nom;
		this.pays = pays;
		this.typeValeur = type;

	}

	public BigDecimal getVariationCoursPourCent() {
		return variationCoursPourCent;
	}

	private void computeVariation (double oldValeur, double newValeur) {
		double diff = oldValeur - newValeur;

		this.variationCoursPourCent = new BigDecimal(diff / oldValeur * 100).setScale(scale,roundingMode);

	}
	
	public void nextAleatoire(){
		double oldValeur = this.cours.getValeurCours().doubleValue();
		double newValeur = generateNewAleaValeur();
		this.cours = new Cours(newValeur);
		computeVariation(oldValeur,newValeur);
		
	} 
	public Cours getCours() {
		return cours;
	}

	public String getNom() {
		return nom;
	}

	public Pays getPays() {
		return pays;
	}

	public String getIdentifiant() {
		return identifiant;
	}


	private double generateNewAleaValeur () {
		
		double pourcent = (double)(new Random().nextInt(10))/(double)100;

		int sensModification = new Random().nextInt(10)%2;

		double lastValeur = this.cours.getValeurCours().doubleValue();
		double nextValeur;
				
		if(sensModification == 0){
			nextValeur = lastValeur * pourcent + lastValeur;
		}else{
			nextValeur = lastValeur - lastValeur * pourcent;
		}
		
		return nextValeur;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ValeurBoursiere)) return false;

		ValeurBoursiere valeurBoursiere = (ValeurBoursiere) o;

		return identifiant.equals(valeurBoursiere.identifiant);

	}

	@Override
	public int hashCode() {
		return identifiant.hashCode();
	}
}

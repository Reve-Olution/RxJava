package cotation;

import java.math.BigDecimal;



public class Indice {

	private String nom;
	private Cours cours;
	
	
	public Indice(String nom, Cours cours) {
		this.nom = nom;
		this.cours = cours;
	}
	
	
	public void defineCours(){

		this.cours = new Cours(new BigDecimal(generateNewAleaValeur()));
		
	} 
	public Cours getCours() {
		return cours;
	}
	
	public String getNom() {
		return nom;
	}

	private double generateNewAleaValeur () {
		
		double pourcent = (Math.random()*10)/100;
		System.out.println(pourcent);
		
		int sensModification = (int)(Math.random()*10)%2;
		System.out.println(sensModification);
		
		double lastValeur = this.cours.getValeurCours().doubleValue();
		double nextValeur;
				
		if(sensModification == 0){
			nextValeur = lastValeur * pourcent + lastValeur;
		}else{
			nextValeur = lastValeur - lastValeur * pourcent;
		}
		
		return nextValeur;
	}
	
	
}

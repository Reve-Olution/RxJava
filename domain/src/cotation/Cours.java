package cotation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;


public class Cours {

	public  static final int scale = 2;
	public static final RoundingMode roundingMode = RoundingMode.HALF_UP;
	
	private Date dateValeur;
	private BigDecimal valeurCours;
	
	
	public Cours(double valeurCours) {
		this.valeurCours = new BigDecimal(valeurCours).setScale(scale,roundingMode);
		this.dateValeur = new Date();
	}
	
	public Cours(BigDecimal valeurCours) {
		this.valeurCours = valeurCours.setScale(scale,roundingMode);
		this.dateValeur = new Date();
	}
	
	public Date getDateValeur () {
		return dateValeur;
	}
	
	public BigDecimal getValeurCours() {
		return valeurCours;
	}


}

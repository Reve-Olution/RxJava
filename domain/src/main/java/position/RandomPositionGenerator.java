package position;

import java.util.Date;

public class RandomPositionGenerator {

	private Position lastPosition;
    private int compteurOperateur = 0;
    private char operateur = '+';
	
	/**
	 * Constructeur prenant en paramétre le premier point GPS à générer.
	 * @param startPosition
	 */
	public RandomPositionGenerator(Position startPosition){
		lastPosition = startPosition;
	}
	
	/**
	 * Retourne la prochaine position basé sur la dernière générée.
	 * Incrément ou décrémente de 1° maximum la latitude et le longitude:
	 * @return la prochaine position
	 */
	public Position next() {
		
		double latitude = moveCoordinateValue(lastPosition.lat());
		double longitude = moveCoordinateValue(lastPosition.lng());

		TrackingObject trackingObject = lastPosition.trackingObject();
		
		lastPosition = new Position(latitude, longitude, new Date(),trackingObject);
		
		return lastPosition;
	}

	private double moveCoordinateValue (double initPosition) {

        if(compteurOperateur < 10){
            compteurOperateur++;
        }else{
            compteurOperateur = 0;
            if(operateur == '+'){
                operateur = '-';
            }else{
                operateur = '+';
            }
        }

        if(operateur == '+'){
            return additione(initPosition);
        }else{
            return soustrait(initPosition);
        }
	}

    private double additione (double valeur) {
        return valeur + Math.random();
    }

    private double soustrait (double valeur) {
        return valeur - Math.random();
    }
}

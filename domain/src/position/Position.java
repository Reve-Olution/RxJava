package position;

import java.util.Date;

public class Position {
	private double lat;
	private double lng;
	private Date datePosition;
	private TrackingObject trackingObject;
	
	public static final int MIN_LONG = 1;
	public static final int MAX_LONG = 10;
	public static final int MIN_LAT = 40;
	public static final int MAX_LAT = 50;

	
	enum TypeValeur{
		LATITUDE, LONGITUDE;
	};
	
	public double lat(){
		return this.lat;
	}
	
	public double lng(){
		return this.lng;
	}
	
	public Date datePosition(){
		return datePosition;
	}
	
	public TrackingObject trackingObject () {
		return trackingObject;
	}
	
	
	
	public Position(double lat, double lng, Date datePosition, TrackingObject trackingObject) {
		this.lat = lat;
		this.lng = lng;
		this.datePosition = datePosition;
		this.trackingObject = trackingObject;
	}
	
	public static Position generateSample(TrackingObject trackingObject){
		
		return new Position(randomLatitude(),randomLongitude(),new Date(),trackingObject);
		
	}

	public static double randomLongitude() {
		
		return randomValueFor(TypeValeur.LONGITUDE);
	}
	
	public static double randomLatitude() {
		
		return randomValueFor(TypeValeur.LATITUDE);
	}
	
	private static double randomValueFor(TypeValeur type) {
		
		double valMax = 0;
		double valMin = 0;
		
		switch(type){
			
			case LATITUDE:
				valMax = MAX_LAT;
				valMin = MIN_LAT;
			break;
			
			case LONGITUDE:
				valMax = MAX_LONG;
				valMin = MIN_LONG;
			break;
		}
		
		double valeur = 0;
		
		while(valeur < valMin || valeur > valMax){
			valeur = Math.random() * valMax;
		}
		return valeur;
	}



	
}
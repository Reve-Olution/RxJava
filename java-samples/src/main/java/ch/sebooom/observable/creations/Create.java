package ch.sebooom.observable.creations;

import rx.Observable;
import rx.Subscriber;
import rx.internal.operators.OperatorObserveOn;

/**
 * Exemples de codes basée sur la création d'Observable avec la méthode statique create
 * @author seb
 *
 */
public class Create {

	
	
	public static void main(String[] args) {
		create();
		create_java7();
		create_2();
	}
	
	/**
	 * Creation d'un observable from scratch
	 */
	public static void create(){
		System.out.println("****** Observable standard create *****");
		Observable.create(subscriber -> {
			try{
				if(!subscriber.isUnsubscribed()){
					for(int val = 0; val < 10; val ++){
						subscriber.onNext(val);
					}
				}
				subscriber.onCompleted();
				
			}catch (Exception e){
				subscriber.onError(e);
			}
		}).subscribe(
				//on next
				(item) -> {System.out.println("item: " + item);},
				//on error
				(error) -> {System.out.println(error);},
				//on completed
				() -> {System.out.println("Completed");}
		);
	}
	
	/**
	 * Creation d'un observable from scratch java 7 style
	 */
	public static void create_java7(){
		System.out.println("****** Observable standard create java 7 *****");
		Observable.create(new Observable.OnSubscribe<Integer>() {
		    @Override
		    public void call(Subscriber<? super Integer> subscriber) {
		        try {
		            if (!subscriber.isUnsubscribed()) {
		                for (int i = 1; i < 5; i++) {
		                    subscriber.onNext(i);
		                }
		                subscriber.onCompleted();
		            }
		        } catch (Exception e) {
		            subscriber.onError(e);
		        }
		    }
		 } ).subscribe(new Subscriber<Integer>() {
		        @Override
		        public void onNext(Integer item) {
		            System.out.println("Next: " + item);
		        }

		        @Override
		        public void onError(Throwable error) {
		            System.err.println("Error: " + error.getMessage());
		        }

		        @Override
		        public void onCompleted() {
		            System.out.println("Sequence complete.");
		        }
		    });
	}

	/**
	 * Observable avec découpage et références
	 */
	public static void create_2 () {
		System.out.println("****** Observable standard create with filters *****");
		//Observable retournant une suite de nombre entier
		Observable<Integer> fluxEntier = Observable.create(subscriber -> {
			try{
				if(!subscriber.isUnsubscribed()){
					for(int val = 0; val < 100; val++){
						subscriber.onNext(val);
					}
				}
				subscriber.onCompleted();
				
			}catch (Exception e){
				subscriber.onError(e);
			}
		});
	
		//Un subscriber est une implementation par defaut de Observer
		Subscriber<Integer> observer = new Subscriber<Integer>() {

			public void onNext(Integer item) {
				System.out.println("Next: " + item);
			}

	        @Override
	        public void onError(Throwable error) {
	            System.err.println("Error: " + error.getMessage());
	        }
	
	        @Override
	        public void onCompleted() {
	            System.out.println("Sequence complete.");
	        }
	    };
	    
	    //souscription au flux de l'observable
        fluxEntier.subscribe(observer);
        
        System.out.println("****** Observable standard create with filters second subscribe *****");
        //Idem, methode light j8
        fluxEntier.subscribe(
        		(item)->{System.out.println("Item:" + item);},
        		(error)->{System.out.println("Error:" + error.getMessage());},
        		()->{System.out.println("Flux terminé");}
        );
        
        System.out.println("****** Observable standard create with filters odd numbers*****");
        //Spécialisation de l'obersavble afin de ne retourner que des nombes pairs
        Observable<Integer> integerPairsObservable = fluxEntier.filter(item -> {
        	return item % 2 == 0;
        });
        
        
        integerPairsObservable.subscribe(
        		(item)->{System.out.println("Item:" + item);},
        		(error)->{System.out.println("Error:" + error.getMessage());},
        		()->{System.out.println("Flux termin�");}
        );
        
	}

	/**
	 * Retourne un observable émettant une suite de nombre jusqu'à la valeur max passé en paramètre
	 * @param le nombre max qui sera retourné par l'Observable
	 * @return un observable
	 */
	public static Observable<Integer> get100IntegerObservable (int max) {
		
		return Observable.create(observable -> {
			try{
				if(!observable.isUnsubscribed()){
					for(int val = 0; val < max; val ++){
						observable.onNext(val);
					}
				}
				//notification de fin de flux
				observable.onCompleted();
				
			}catch (Exception e){
				observable.onError(e);
			}
		});
	}
}

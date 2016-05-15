package ch.sebooom.observable.creations;


import ch.sebooom.orchestrate.User;
import rx.Observable;
import rx.Observer;


public class Just {

	public static void main(String[] args) {
		just();
		just_j8_light();
		justMultipleParameters();
	}
	
	public static void just() {
		System.out.println("****** Observable standard just *****");
		Observable<User> observable = Observable.just(new User("Seb","Seb"));
		
		Observer<User> observer = new Observer<User>() {

			@Override
			public void onCompleted() {
				System.out.println("Complete");
				
			}

			@Override
			public void onError(Throwable e) {
				System.out.println(e);
				
			}

			@Override
			public void onNext(User t) {
				System.out.println(t);
				
			}
		};
		
		observable.subscribe(observer);
	}
	
	public static void just_j8_light () {
		System.out.println("****** Observable standard just j8 *****");
		Observable.just(new User("Seb","Seb"))
				.subscribe(user -> {
					System.out.println(user);
				}
		);
	}
	
	public static void justMultipleParameters () {
		System.out.println("****** Observable standard just multiple objects *****");
		Observable.just(new User("Seb","Seb"),new User("tutu","toto"), new User("Mickey","mouse"))
			.subscribe(user -> {
				System.out.println(user);
			}
		);
	}
	
	
}

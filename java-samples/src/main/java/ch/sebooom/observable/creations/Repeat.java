package ch.sebooom.observable.creations;

import java.util.concurrent.TimeUnit;

import rx.Observable;

public class Repeat {

	public static void main(String[] args) {
		repeat();

	}
	
	public static void repeat () {
		Observable.from(new String[]{"un","deux","trois"})
				.repeat(3)
				.filter(item -> {
					return item.equals("un");
				})
				.subscribe(str -> {
					System.out.println(str);
				}
		);
	}


	
	

}

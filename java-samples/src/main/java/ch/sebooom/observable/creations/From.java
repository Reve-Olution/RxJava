package ch.sebooom.observable.creations;

import ch.sebooom.observable.sample.StringCallable;
import ch.sebooom.orchestrate.User;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class From {

	public static void main(String[] args) {
		from();
		fromFuture();
	}
	
	public static void from () {
		System.out.println("****** Observable standard from array *****");
		Observable.from(new String[]{"un","deux","trois","quatre"})
			.subscribe(item -> {
				System.out.println(item);
			}
		);
		
		System.out.println("****** Observable standard from iterable *****");
		List<User> users = new ArrayList<>();
		users.add(new User("toto","tutu"));
		users.add(new User("tato","tytu"));
		users.add(new User("tyty","tata"));
		
		Observable.from(users)
			.subscribe(item -> {
				System.out.println(item);
			}
		);
		
	}
	
	public static void fromFuture () {
		System.out.println("****** Observable standard from Future *****");
		
		StringCallable callable1 = new StringCallable(3000);
        StringCallable callable2 = new StringCallable(2000);
 
        FutureTask<String> futureTask1 = new FutureTask<String>(callable1);
        FutureTask<String> futureTask2 = new FutureTask<String>(callable2);
        
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(futureTask1);
        //executor.execute(futureTask2);
        
        Observable.from(futureTask1)
        	.subscribe(
        		(future) -> {System.out.println(future);},
        		(error) -> {},
        		() -> {System.out.println("Done");}
        );
        
        System.out.println("Fin méthode");
 
	}
}

package ch.sebooom.future;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by seb on 30.05.16.
 */
public class IntegerCallableNonBlocking {



    public static void main(String[] args) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new IntegerCallable());

        System.out.println("Apres submit");

        //shutdown une fois que tout est terminé
        executor.shutdown();

    }
}

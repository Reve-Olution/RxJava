package ch.sebooom.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by seb on 30.05.16.
 */
public class IntegerCallableBlocking {

    public static void main(String[] args) {

        ExecutorService executor = Executors.newSingleThreadExecutor();

        //On récupère un objet Future<V>
        Future<Integer> future = executor.submit(new IntegerCallable());


        try {
            System.out.println("Callable retour: " + future.get());

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("Apres submit");

        //shutdown une fois que tout est terminé
        executor.shutdown();
    }
}

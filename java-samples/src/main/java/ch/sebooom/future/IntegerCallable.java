package ch.sebooom.future;

import java.util.concurrent.Callable;

/**
 * Created by seb on 30.05.16.
 */
public class IntegerCallable implements Callable<Integer> {

    @Override
    public Integer call() throws Exception  {

        try {
            Thread.sleep(4000);
            System.out.println("Apres 4 secondes...");
        }
        catch(InterruptedException e){
            throw new Exception("Thread interrompu ; cause " + e.getMessage());
        }
        return 3;
    }

}

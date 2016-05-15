package ch.sebooom.observable.sample;

import java.util.concurrent.Callable;

public class StringCallable  implements Callable<String>{

		 
	    private long waitTime;
	     
	    public StringCallable(int timeInMillis){
	        this.waitTime=timeInMillis;
	    }
	    
	    @Override
	    public String call() throws Exception {
	        Thread.sleep(waitTime);
	        return Thread.currentThread().getName();
	    }
	 
	
}

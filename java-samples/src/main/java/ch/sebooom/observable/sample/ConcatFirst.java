package ch.sebooom.observable.sample;

import rx.Observable;

import java.util.logging.Logger;

/**
 * Created by seb on 12.05.16.
 */
public class ConcatFirst {

    final static Logger log = Logger.getLogger(ConcatFirst.class.getName());

    public static void main(String[] args) throws InterruptedException {

        ConcatFirst f = new ConcatFirst();


        Observable<Test> source = Observable.concat(
                f.first(),
                f.second(),
                f.third()

        ).first(data -> {
            return data!= null && data.getOk();
        });


//        Observable.interval(1, TimeUnit.SECONDS)
//                .flatMap(__ -> source)
//                .subscribe(data -> System.out.println("Received: " + data.getNom()));

        source.subscribe(test -> {
           log.info(""+test.getNom());
        });

        Thread.sleep(15000);
    }

    private  Observable<Test> first() throws InterruptedException {
        Observable obs = Observable.create(subscriber -> {
            log.info("FIRST");
            subscriber.onNext(new Test("first",false));
            subscriber.onCompleted();
        });

        //Observable obs = Observable.just(new Test("first",true));


        return obs;
    }

    private  Observable<Test> second() throws InterruptedException {
        Observable obs = Observable.create(subscriber -> {
            log.info("SECOND");
            subscriber.onNext(new Test("second",true));
            subscriber.onCompleted();
        });

        //Observable obs = Observable.just(new Test("second",false));

        return obs;
    }

    private  Observable<Test> third() throws InterruptedException {
        Observable obs = Observable.create(subscriber -> {
            log.info("THIRD");
            subscriber.onNext(new Test("third",true));
            subscriber.onCompleted();
        });

        //Observable obs = Observable.just(new Test("third",false));
        return obs;
    }


    class Test {
        private String nom;
        private Boolean ok;

        public Test(String nom, Boolean ok) {
            log.info("Construct for [" + nom + "]");
            this.nom = nom;
            this.ok = ok;
        }

        public String getNom() {
            return nom;
        }

        public Boolean getOk() {
            return ok;
        }
    }

}

package ch.sebooom.observable.async;

import rx.Observable;
import rx.observables.MathObservable;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by seb on 17.05.16.
 */
public class UsersCollection {

    static List<User> utilisateurs = new ArrayList<>();
    
    static{
        utilisateurs.add(new User("sce","sc1212",22, User.Sexe.MASCULIN,true));
        utilisateurs.add(new User("dda","sd*esda",19, User.Sexe.FEMININ,true));
        utilisateurs.add(new User("gtr","%_redsd",55, User.Sexe.FEMININ,true));
        utilisateurs.add(new User("bvd","jnhd561",33, User.Sexe.MASCULIN,false));
        utilisateurs.add(new User("iun","loWs2",29, User.Sexe.MASCULIN,true));
        utilisateurs.add(new User("olu","rewf5d",44, User.Sexe.MASCULIN,true));
        utilisateurs.add(new User("oca","bibi",21, User.Sexe.FEMININ,true));
        utilisateurs.add(new User("ztp","dsada23",19, User.Sexe.MASCULIN,true));
        utilisateurs.add(new User("bnh","12w34fd",40, User.Sexe.MASCULIN,true));
        utilisateurs.add(new User("lkj","990xx",61, User.Sexe.FEMININ,true));
        utilisateurs.add(new User("esx","slol",55, User.Sexe.MASCULIN,true));
        utilisateurs.add(new User("xxs","lyasde",51, User.Sexe.MASCULIN,false));
        utilisateurs.add(new User("wsy","gftrr",37, User.Sexe.FEMININ,true));
        utilisateurs.add(new User("wwe","wwe9999",38, User.Sexe.MASCULIN,true));
        utilisateurs.add(new User("uzt","09lo98iu",42, User.Sexe.MASCULIN,true));
    }
    
    public static void main(String[] args) {

        //premierExemple();
        //detailOperatorLog();
        //subscribeOnOtherThread();
        parallelExample();
        //simpleNonBlockingMultiThread();
    }

    public static void premierExemple () {

        Observable<User> allUsers = getUserObservable();

        Observable<User> femmes = allUsers.filter(user -> {
            return user.isFromSexe(User.Sexe.FEMININ);
        });

        Observable<User> homme = allUsers.filter(user -> {
            return user.isFromSexe(User.Sexe.MASCULIN);
        });


        Observable<Double> agesHomme = allUsers.map(user -> {return new Double(user.age());});

        Observable<Integer> ageMaxHomme = MathObservable.max(allUsers.map(user -> {
            return user.age();
        }));


        log("********************** All users");
        allUsers.subscribe(user -> {
           log(user.login());
        });

        log("********************* All homme");
        homme.subscribe(user -> {
            log(user.login());
        });

        log("********************* Age max");
        ageMaxHomme.subscribe(age -> {
            log("Age max:" + age);
        });

    }

    public static void synchroneTest () {

    }

    /**
     * Retourne un observable basé sur une utilisateurs d'utilisateur
     * @return un observable émettant les utilisateurs d'une utilisateurs
     */
    public static Observable<User> getUserObservable () {
        Observable<User> obs = Observable.from(utilisateurs);
        return obs;
    }

    public static Observable<User> getUserObservableWith2sSleep () {
        Observable<User> obs = Observable.from(utilisateurs);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return obs;
    }



    /**
     * Exemple bloquant. Le traitement complet s'execiute dans le thread courant
     * @return la utilisateurs des utilisateurs émits par l'observable
     */
    public static void detailOperatorLog () {
        log("****************** detailOperatorLog");

        List<User> users = new ArrayList<>();

        getUserObservableWith2sSleep()
                .doOnNext(user-> log("Initial step on next: " + user.login()))
                .doOnCompleted(() -> log("Complete Initial on next"))

                .filter(user->{
                    return user.login().startsWith("s") || user.login().startsWith("w");
                })
                .doOnNext(user-> {
                    log("Filter login start with s or a: " + user.login());})
                .doOnCompleted(() -> log("Complete Filter login"))

                .subscribe(
                user->{
                    log("Subscriber onNext: " + user.login());
                },
                error -> {},
                () -> {});

        log("detailOperatorLog method end");
    }


    public static void parallelExample () {

        log("****************** parallelExample");

        Observable<User> obs = getUserObservable();

        obs.flatMap(
                user -> Observable.just(user)
                .subscribeOn(Schedulers.computation())
                .map(u -> calculLong(u)))
            .subscribe(valeurCalcul -> {
                log(String.valueOf(valeurCalcul));
            });

        sleep(10000);

        log("****************** parallelExample end");


    }

    static void sleep (int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static Integer calculLong(User user) {
        log("Start calculation for user: "  + user.login());
        try {
            Thread.sleep(new Random().nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log("Calulation ended for user: " + user.login());
        return new Random().nextInt(100);
    }

    /**
     * Exemple bloquant. Le traitement complet s'execiute dans le thread courant
     * @return la utilisateurs des utilisateurs émits par l'observable
     */
    public static void subscribeOnOtherThread () {
        log("****************** subscribeOnOtherThread start");

        List<User> users = new ArrayList<>();

        Observable<User> obs = getUserObservable();

        obs.subscribeOn(Schedulers.newThread()).subscribe(
                        user->{
                            log("subscriber 1 onNext: " + user.login());
                        },
                        error -> {},
                        () -> {
                            log("subscriber 1 complete");
                        });

        obs.subscribeOn(Schedulers.newThread()).subscribe(
                user->{
                    log("subscriber 2 onNext: " + user.login());
                },
                error -> {},
                () -> {
                    log("subscriber 2 complete");
                });


        log("subscribeOnOtherThread method end");
    }


    public static void simpleNonBlockingMultiThread () {
        log("****************** SimpleNonBlockingMultiThread start");

        List<User> users = new ArrayList<User>();

        getUserObservable()
                .subscribeOn(Schedulers.computation())
                .filter(user -> {
                    log("subscriber filter: " + user.login());
                    return user.age() > 50;
                })
                .observeOn(Schedulers.computation())
                .subscribe(
                        user->{
                            log("subscriber onNext: " + user.login());
                            users.add(user);
                        },
                        error -> {
                            log("Observable error");
                        },
                        () -> {
                            log("Observable complete");
                        });
        log("simpleNonBlockingMultiThread method end");


    }

    /**
     * Exemple bloquant. Le traitement complet s'execiute dans un pool alloué par rx
     * @return la utilisateurs des utiutilisateursurs émits par l'observable
     */
    public static void simpleNonBlockingSample () {
        log("****************** SimpleNonBlockinSample start");

        List<User> users = new ArrayList<User>();

        getUserObservable()
            .subscribeOn(Schedulers.computation())
            .subscribe(
                user->{
                    log("subscriber onNext: " + user.login());
                    users.add(user);
                },
                error -> {
                    log("Observable error");
                },
                () -> {
                    log("Observable complete");
                });
        log("SimpleNonBlockinSsample method end");


    }





    static void log(String msg) {
        System.out.println("["+Thread.currentThread().getName()+"] - " + msg);
    }







}

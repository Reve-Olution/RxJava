package ch.sebooom.observable.async;

import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by seb on 17.05.16.
 */
public class UsersCollection {


    /**
     * Retourne un observable basé sur une liste d'utilisateur
     * @return un observable émettant les utilisateurs d'une liste
     */
    public Observable<User> getUserObservable () {
        Observable<User> obs = Observable.from(getUSersList());
        return obs;
    }

    /**
     * Exemple bloquant. Le traitement complet s'execiute dans le thread courant
     * @return la liste des utilisateurs émits par l'observable
     */
    public List<User> simpleBlockingSample () {
        logThreadName("SimpleBlockinSample start");

        List<User> users = new ArrayList<>();

        getUserObservable()
            .subscribe(
                user->{
                    logThreadName("subscriber onNext: " + user.login());
                    users.add(user);
                },
                error -> {},
                () -> {});

        logThreadName("SimpleBlockinSsample end, before return");

        return users;
    }

    public List<User> simpleNonBlockingMultiThread () {
        logThreadName("simpleNonBlockingMultiThread");

        List<User> users = new ArrayList<User>();

        getUserObservable()
                .subscribeOn(Schedulers.computation())
                .filter(user -> {
                    logThreadName("subscriber filter: " + user.login());
                    return user.age() > 50;
                })
                .observeOn(Schedulers.computation())
                .subscribe(
                        user->{
                            logThreadName("subscriber onNext: " + user.login());
                            users.add(user);
                        },
                        error -> {
                            logThreadName("Observable error");
                        },
                        () -> {
                            logThreadName("Observable complete");
                        });
        logThreadName("simpleNonBlockingMultiThread end, before return");

        return users;
    }

    /**
     * Exemple bloquant. Le traitement complet s'execiute dans un pool alloué par rx
     * @return la liste des utilisteurs émits par l'observable
     */
    public List<User> simpleNonBlockingSample () {
        logThreadName("SimpleNonBlockinSample start");

        List<User> users = new ArrayList<User>();

        getUserObservable()
            .subscribeOn(Schedulers.computation())
            .subscribe(
                user->{
                    logThreadName("subscriber onNext: " + user.login());
                    users.add(user);
                },
                error -> {
                    logThreadName("Observable error");
                },
                () -> {
                    logThreadName("Observable complete");
                });
        logThreadName("SimpleNonBlockinSsample end, before return");


        return users;
    }





    static void logThreadName (String msg) {
        System.out.println("["+Thread.currentThread().getName()+"] - " + msg);
    }




    static List getUSersList () {
        List<User> liste = new ArrayList<>();
        liste.add(new User("sce","sc1212",22, User.Sexe.MASCULIN,true));
        liste.add(new User("dda","sd*esda",19, User.Sexe.FEMININ,true));
        liste.add(new User("gtr","%_redsd",55, User.Sexe.FEMININ,true));
        liste.add(new User("bvd","jnhd561",33, User.Sexe.MASCULIN,true));
        liste.add(new User("iun","loWs2",29, User.Sexe.MASCULIN,true));
        liste.add(new User("olu","rewf5d",44, User.Sexe.MASCULIN,true));
        liste.add(new User("oca","bibi",21, User.Sexe.FEMININ,true));
        liste.add(new User("ztp","dsada23",19, User.Sexe.MASCULIN,true));
        liste.add(new User("bnh","12w34fd",40, User.Sexe.MASCULIN,true));
        liste.add(new User("lkj","990xx",61, User.Sexe.FEMININ,true));
        liste.add(new User("esx","slol",55, User.Sexe.MASCULIN,true));
        liste.add(new User("xxs","lyasde",51, User.Sexe.MASCULIN,true));
        liste.add(new User("wsy","gftrr",37, User.Sexe.FEMININ,true));
        liste.add(new User("wwe","wwe9999",38, User.Sexe.MASCULIN,true));
        liste.add(new User("uzt","09lo98iu",42, User.Sexe.MASCULIN,true));


        return liste;
    }


}

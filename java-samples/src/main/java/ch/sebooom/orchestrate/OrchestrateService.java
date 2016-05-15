package ch.sebooom.orchestrate;


import io.orchestrate.client.*;
import rx.Observable;



/**
 * Created by seb on 30.04.16.
 */
public class OrchestrateService {

    private static OrchestrateClient client;

    private OrchestrateService () {

    }

    public static void main(String[] args) throws InterruptedException {

        client =  OrchestrateClient.builder("20d9a007-a7d7-4bbd-b972-8141b1f3bf03")
                .host("https://api.aws-us-east-1.orchestrate.io")
                .build();


        synchronousBlockinCall();
        asynchronousBlockinCall();



    }

    private static void asynchronousBlockinCall() throws InterruptedException {

        System.out.println("asynchronousBlockinCall start");


        OrchestrateRequest<KvList<User>> results =
                client.listCollection("users")
                        .get(User.class)
                        .on(new ResponseListener<KvList<User>>() {

                            public void onFailure(Throwable throwable) {
                                System.out.println(throwable.getMessage());
                            }


                            public void onSuccess(KvList<User> kvObjects) {

                                System.out.println("on success");

                                Observable.from(kvObjects)
                                        .subscribe(
                                                item -> {System.out.println("as:" + item);}

                                        );



                            }
                        });



        System.out.println("asynchronousBlockinCall end");

        Thread.sleep(5000);

    }

    private static void synchronousBlockinCall() {

        System.out.println("synchronousBlockinCall start");

        OrchestrateClient client = OrchestrateClient.builder("20d9a007-a7d7-4bbd-b972-8141b1f3bf03")
                .host("https://api.aws-us-east-1.orchestrate.io")
                .build();

        KvList<User> results =
                client.listCollection("users")
                        .get(User.class)
                        .get();

        for (KvObject<User> userKv : results) {
            // do something with the object
            System.out.println(userKv.getValue());
        }

        System.out.println("synchronousBlockinCall end");

    }


}

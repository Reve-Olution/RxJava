package bourse;

/**
 * Created by seb on 29.05.16.
 */
public class IndicesTest {


    public static void main(String[] args) {
        new IndicesTest().testInstanceCreation();
    }

    public void testInstanceCreation () {
        ValeurBoursiere c = new ValeurBoursiere("test","test",new Cours(2345.56),Pays.FRANCE,TypeValeurs.ACTION);


        System.out.println(c.getCours().getValeurCours());

        c.nextAleatoire();

        System.out.println(c.getCours().getValeurCours());

        c.nextAleatoire();

        System.out.println(c.getCours().getValeurCours());

        c.nextAleatoire();

        System.out.println(c.getCours().getValeurCours());

        c.nextAleatoire();

        System.out.println(c.getCours().getValeurCours());

        c.nextAleatoire();

        System.out.println(c.getCours().getValeurCours());




    }

}
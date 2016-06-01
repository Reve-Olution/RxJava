package ch.sebooom.comparaison;

import org.junit.Before;
import org.junit.Test;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ch.sebooom.comparaison.Personne.Sexe.FEMME;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by seb on 30.05.16.
 */
public class TriListeTest {

    List<Personne> personnes = new ArrayList<>();

    @Before
    public void setUp () {
        personnes.add(Personne.getFemme("Hilton","Paris",30));
        personnes.add(Personne.getFemme("Minnie","Mouse",22));
        personnes.add(Personne.getHomme("LaSaumure","Dodo",68));
        personnes.add(Personne.getFemme("Johansson","Scarlet",32));
        personnes.add(Personne.getHomme("Obama","Barack",55));
        personnes.add(Personne.getHomme("Moi","Lui",99));
        personnes.add(Personne.getHomme("Tutu","ToTo",12));
        personnes.add(Personne.getHomme("Oui","Oui",10));
        personnes.add(Personne.getFemme("Exploratrice","Dora",18));
        personnes.add(Personne.getHomme("Skywalker","Luke",20));
        personnes.add(Personne.getFemme("Sifredi","Katsumi",31));
        personnes.add(Personne.getFemme("Anderson","Pamela",39));
        personnes.add(Personne.getFemme("Hilton","Paris",30));
        personnes.add(Personne.getHomme("Dubois","Jacques",45));
    }

    @Test
    public void testSimpleListFilter () {

        //*********** old old java style ***********
        List<Personne> femmesMoinsDe30 = new ArrayList<>();

        for(Personne p : personnes){
            if(p.age() < 30 && p.sexe() == FEMME){
                femmesMoinsDe30.add(p);
            }
        }

        assertTrue("personnes size after filter:" + femmesMoinsDe30.size(),femmesMoinsDe30.size() == 2);

        //*********** java 8 style ***********
        femmesMoinsDe30 = personnes.stream()
                .filter(personne -> {
                    return personne.sexe() == FEMME && personne.age() < 30;
                })
                .collect(Collectors.toList());

        assertThat(femmesMoinsDe30).hasSize(2);


        //********** rxJava style ************
        Observable.from(personnes)
               .filter(personne -> {
                   return personne.sexe() == FEMME && personne.age() < 30;
               })
                .toList()
                .subscribe(listPersonne -> {
                    assertThat(listPersonne).hasSize(2);
                });

    }

    @Test
    public void testGeneratePersonInitial(){
        //*********** old old java style ***********
        List<String> initiales = new ArrayList<>();

        for(Personne p : personnes){
            initiales.add(p.nom().charAt(0) +""+ p.prenom().charAt(0));
        }

        assertThat(initiales).hasSize(14);

    }


}

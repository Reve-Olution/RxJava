package ch.sebooom;

import bourse.ValeurBoursiere;
import bourse.Indices;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Created by seb on 22.05.16.
 */
public class MarketIndexSampleFlowTest {

    @Test
    public void testRandomIndice() {

        List<ValeurBoursiere> valeurBoursieres = Indices.getIndicesAsList();

        MarketIndexSampleFlow f = new MarketIndexSampleFlow();


        for (int cpt = 0; cpt < 1000; cpt++) {
            assertNotNull(f.getIndiceAlea());
        }
    }

    @Test
    public void testRandomRange() {
        List<ValeurBoursiere> valeurBoursieres = Indices.getIndicesAsList();

        MarketIndexSampleFlow f = new MarketIndexSampleFlow();

        Map<String, Integer> occurencesByIndice = new HashMap<>();

        for (ValeurBoursiere i : valeurBoursieres) {
            occurencesByIndice.put(i.getIdentifiant(), 0);
        }


        int nbreIterations = 10000;
        int differenceMax = 100;

        for (int cpt = 0; cpt < nbreIterations; cpt++) {
            ValeurBoursiere valeurBoursiere = f.getIndiceAlea();

            Integer c = occurencesByIndice.get(valeurBoursiere.getIdentifiant());

            occurencesByIndice.put(valeurBoursiere.getIdentifiant(), ++c);

            assertNotNull(f.getIndiceAlea());
        }

        int valMoyenne = nbreIterations / occurencesByIndice.keySet().size();

        for (String indice : occurencesByIndice.keySet()) {
            int nbreOccurences = occurencesByIndice.get(indice).intValue();
            System.out.println(indice + " : " + nbreOccurences);

            // int diff = Math.abs(nbreOccurences - valMoyenne);
            //assertTrue("Diff: " + diff +" expected lower than differenceMax :" + differenceMax,diff < differenceMax);
        }
    }

    @Test
    public void testMessageEmission() {

        new MarketIndexSampleFlow().start();
    }
}

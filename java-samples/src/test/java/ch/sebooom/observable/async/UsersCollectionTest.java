package ch.sebooom.observable.async;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Created by seb on 18.05.16.
 */
public class UsersCollectionTest {

    @Rule
    public TestName name = new TestName();

    @Before
    public void before(){
        System.out.println("*************************************************************");
        System.out.println("TEST: " + name.getMethodName());
    }

    @Ignore
    @Test
    public void simpleBlockinSampleTest () {
        List<User> users =  new UsersCollection().simpleBlockingSample();
        assertTrue("Effective users size : " + users.size() ,users.size() > 0);
    }

    static void printSetContent(Set<String> elements){
        System.out.println("Threads using:");

        for(String chaine : elements){
            System.out.println(chaine);
        }
    }

    @Ignore
    @Test
    public void simpleNonBlockinSampleTestWithouSleep () throws InterruptedException {
        List<User> users = new UsersCollection().simpleNonBlockingSample();
        assertTrue("Effective threads size : " + users.size() ,users.size() == 0);
    }

    @Ignore
    @Test
    public void simpleNonBlockinSampleTestWithSleep () throws InterruptedException {
        List<User> users = new UsersCollection().simpleNonBlockingSample();
        Thread.sleep(2000);
        assertTrue("Effective list size : " + users.size() ,users.size() > 0);
    }


    @Test
    public void simpleNonBlockinMultiThreadSampleTestWithSleep () throws InterruptedException {
        List<User> users = new UsersCollection().simpleNonBlockingMultiThread();
        Thread.sleep(2000);
        assertTrue("Effective list size : " + users.size() ,users.size() > 0);
    }

}
package ch.sebooom.servers;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by seb on 20.05.16.
 */
public class PathsTest {
    @Test
    public void pathsForType() throws Exception {

        List<Paths> pathsWS = Paths.pathsForType(PathType.WEBSOCKET);

        assertNotNull(pathsWS);
        assertFalse("Effective path size: " + pathsWS.size(),pathsWS.size() > 0);

        pathsWS.forEach(path -> {
            assertTrue(path.type().equals(PathType.WEBSOCKET));
        });

    }

}
package Server;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

/**
 * This interface is used to implement caching. Requires a search method if implemented.
 * @param <RESULT>
 * @param <TARGET>
 */
public interface BroadbandInterface<RESULT, TARGET> {
    RESULT search(TARGET target) throws URISyntaxException, IOException, InterruptedException;
}

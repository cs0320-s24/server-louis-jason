package Server.Cache;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * This interface is used to implement a generic search type. Used to implement caching.
 *
 * @param <RESULT>
 * @param <TARGET>
 */
public interface SearchInterface<RESULT, TARGET> {
  RESULT search(TARGET target) throws URISyntaxException, IOException, InterruptedException;
}

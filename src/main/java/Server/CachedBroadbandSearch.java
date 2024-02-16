package Server;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

/**
 * This is the CachedBroadbandSearch class. It is a wrapper class of the BroadbandInterface. This
 * class is used for caching. If the information was already requested, then it will be stored in
 * the cache. If the information is not in the class, then it will call the search method of the
 * BroadbandInterface it is wrapping. The cache can be customized by its caller by passing in
 * maxSize and expireAfterWrite arguments.
 */
public class CachedBroadbandSearch implements BroadbandInterface<String, BroadbandInfo> {

  /**
   * A class that wraps a BroadbandInterface instance and caches responses for efficiency. Notice
   * that the interface hasn't changed at all. This is an example of the proxy pattern; callers will
   * interact with the CachedFileServer, rather than the "real" data source.
   *
   * <p>This version uses a Guava cache class to manage the cache.
   */
  private final BroadbandInterface<String, BroadbandInfo> wrappedSearcher;

  private final LoadingCache<BroadbandInfo, String> cache;

  /**
   * Proxy class: wrap an instance of BroadbandInterface (of any kind) and cache its results.
   *
   * @param toWrap the Searcher to wrap
   * @param maxSize maxSize wanted of the cache
   * @param expireAfterWrite how long it should be in the cache for
   */
  public CachedBroadbandSearch(
      BroadbandInterface<String, BroadbandInfo> toWrap, int maxSize, int expireAfterWrite) {
    this.wrappedSearcher = toWrap;
    this.cache =
        CacheBuilder.newBuilder()
            // How many entries maximum in the cache?
            .maximumSize(maxSize)
            // How long should entries remain in the cache?
            .expireAfterWrite(expireAfterWrite, TimeUnit.MINUTES)
            // Keep statistical info around for profiling purposes
            .recordStats()
            .build(
                // Strategy pattern: how should the cache behave when
                // it's asked for something it doesn't have?
                new CacheLoader<>() {
                  @Override
                  public String load(BroadbandInfo key)
                      throws URISyntaxException, IOException, InterruptedException {
                    System.out.println(
                        "called load for: " + key.getCountyName() + ", " + key.getStateName());
                    // If this isn't yet present in the cache, load it:
                    return wrappedSearcher.search(key);
                  }
                });
  }

  /**
   * Search method in implemented for the interface. Gets info from the cache if possible.
   *
   * @param target instance of BroadbandInfo we're searching for
   * @return
   */
  @Override
  public String search(BroadbandInfo target) {
    // "get" is designed for concurrent situations; for today, use getUnchecked:
    String result = cache.getUnchecked(target);
    // For debugging and demo (would remove in a "real" version):
    System.out.println(cache.stats());
    return result;
  }
}

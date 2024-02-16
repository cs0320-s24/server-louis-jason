package Server;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class CachedBroadbandSearch implements BroadbandInterface<String, BroadbandInfo> {

    /**
     * A class that wraps a FileServer instance and caches responses
     * for efficiency. Notice that the interface hasn't changed at all.
     * This is an example of the proxy pattern; callers will interact
     * with the CachedFileServer, rather than the "real" data source.
     *
     * This version uses a Guava cache class to manage the cache.
     */
        private final BroadbandInterface<String, BroadbandInfo> wrappedSearcher;
        private final LoadingCache<BroadbandInfo, String> cache;

        /**
         * Proxy class: wrap an instance of Searcher (of any kind) and cache
         * its results.
         *
         * There are _many_ ways to implement this! We could use a plain
         * HashMap, but then we'd have to handle "eviction" ourselves.
         * Lots of libraries exist. We're using Guava here, to demo the
         * strategy pattern.
         *        * @param toWrap the Searcher to wrap
         */
        public CachedBroadbandSearch(BroadbandInterface<String, BroadbandInfo> toWrap,
                                     int maxSize, int expireAfterWrite) {
            this.wrappedSearcher = toWrap;

            // Look at the docs -- there are lots of builder parameters you can use
            //   including ones that affect garbage-collection (not needed for Server).
            this.cache = CacheBuilder.newBuilder()
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
                                public String load(BroadbandInfo key)  {
                                    System.out.println("called load for: "+key);
                                    // If this isn't yet present in the cache, load it:
                                    return wrappedSearcher.search(key);
                                }
                            });
        }

        @Override
        public String search(BroadbandInfo target) {
            // "get" is designed for concurrent situations; for today, use getUnchecked:
            String result = cache.getUnchecked(target);
            // For debugging and demo (would remove in a "real" version):
            System.out.println(cache.stats());
            return result;
        }

        // This would have been a more direct way to start on building a proxy
        //  (but I like using Guava's cache)
    /*
    public Collection<String> search(String target) {
        // Pass through: call the wrapped object
        return this.wrappedSearcher.searchLines(target);
    }
     */
    }

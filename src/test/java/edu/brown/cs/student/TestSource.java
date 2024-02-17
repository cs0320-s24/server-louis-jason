package edu.brown.cs.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static spark.Spark.after;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import JsonTypes.BroadbandInfo;
import Server.Broadband.BroadbandHandler;
import Server.Broadband.BroadbandSearch;
import Server.Cache.CachedSearcher;
import com.google.common.cache.CacheStats;
import Server.Csv.CSVDataWrapper;
import Server.Csv.LoadCSVFileHandler;
import Server.Csv.SearchCSVFileHandler;
import Server.Csv.ViewCSVFileHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class TestSource {

    @BeforeEach
    public void setup() {
        // No setup needed
    }

    @AfterEach
    public void tearDown() {
        // No teardown needed
    }

    @Test
    public void testSourceAndCache() throws URISyntaxException, IOException, InterruptedException {
        // Does a Unit test of the Census API for data consistency and caching

        // Create a cached searcher for BroadbandInfo. Set to 10 max cache size and 1 minute expirations
        CachedSearcher<String, BroadbandInfo> cachedBroadbandSearch =
                new CachedSearcher<>(new BroadbandSearch(), 10, 3);

        BroadbandInfo searchInfo = new BroadbandInfo("031", "06");

        String result = cachedBroadbandSearch.search(searchInfo);
        String result2 = cachedBroadbandSearch.search(searchInfo); // Cached version
        Thread.sleep(4000); // Wait 4 seconds
        String result3 = cachedBroadbandSearch.search(searchInfo); // Uncached version again

        CacheStats cacheStats = cachedBroadbandSearch.getStats();
        assertEquals(result, result2);
        assertEquals(result2, result3);
        assertEquals(result, result2);
        assertEquals(cacheStats.hitCount(), 1);
        assertEquals(cacheStats.evictionCount(), 1);
    }
}
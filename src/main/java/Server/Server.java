package Server;

import static spark.Spark.after;

import JsonTypes.BroadbandInfo;
import Server.Broadband.BroadbandHandler;
import Server.Broadband.BroadbandSearch;
import Server.Cache.CachedSearcher;
import Server.Csv.CSVDataWrapper;
import Server.Csv.LoadCSVFileHandler;
import Server.Csv.SearchCSVFileHandler;
import Server.Csv.ViewCSVFileHandler;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import spark.Spark;

/** Top-level class. Contains the main() method which starts Spark and runs the various handlers */
public class Server {

  public static void main(String[] args)
      throws URISyntaxException, IOException, InterruptedException {
    // Create a CSV File wrapper for our future parser.
    CSVDataWrapper<List<String>> dataWrapper = new CSVDataWrapper<List<String>>(null);

    // Create a cached searcher for BroadbandInfo. Set to 10 max cache size and 1 minute
    // expirations.
    CachedSearcher<String, BroadbandInfo> cachedBroadbandSearch =
        new CachedSearcher<>(new BroadbandSearch(), 10, 60);

    // Spark initialization
    int port = 3232;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    // Setting up the handler for the GET /loadcsv and /viewcsv and /searchcsv and /broadband
    // endpoints
    Spark.get("loadcsv", new LoadCSVFileHandler(dataWrapper));
    Spark.get("viewcsv", new ViewCSVFileHandler(dataWrapper));
    Spark.get("searchcsv", new SearchCSVFileHandler(dataWrapper));
    Spark.get("broadband", new BroadbandHandler(cachedBroadbandSearch));
    Spark.init();
    Spark.awaitInitialization();

    // Notify of success
    System.out.println("Server started at http://localhost:" + port);
  }
}

package Server;

import static spark.Spark.after;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import spark.Spark;

/** Top-level class. Contains the main() method which starts Spark and runs the various handlers */
public class Server {

  public static void main(String[] args)
      throws URISyntaxException, IOException, InterruptedException {
    DataWrapper<List<String>> dataWrapper = new DataWrapper<List<String>>(null);
    CachedBroadbandSearch cachedBroadbandSearch =
        new CachedBroadbandSearch(new BroadbandSearch(), 10, 1);
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

    // Notice this link alone leads to a 404... Why is that?
    System.out.println("Server started at http://localhost:" + port);
  }
}

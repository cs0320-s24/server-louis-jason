package Server;

import Searcher.Search;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This is the SearchCSVHandler class. This class is to handle any requests made to the searchcsv
 * path in the server class.
 */
public class SearchCSVFileHandler implements Route {

  private DataWrapper<List<String>> data;

  /**
   * This is the constructor of the SearchCSVFileHandler. This is where we set up the instance of
   * Datawrapper.
   *
   * @param data
   */
  public SearchCSVFileHandler(DataWrapper<List<String>> data) {
    this.data = data;
  }

  /**
   * This handle method needs to be filled by any class implementing Route. When the path set in
   * Server gets accessed, it will fire the handle method. This handle method is used for searching
   * a CSV file that was loaded in.
   *
   * @param request The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   */
  @Override
  public Object handle(Request request, Response response) {
    // get values that were passed for the search
    String value = request.queryParams("value");
    String booleanHeader = request.queryParams("booleanHeader");
    String identifier = request.queryParams("identifier");
    String booleanIdentifierAnInt = request.queryParams("booleanIdentifierAnInt");

    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // get the parsed list from the datawrapper
      List<List<String>> objectList = this.data.parseCSV();
      // convert the necessary string arguments into booleans
      boolean booleanHeaderP = Boolean.parseBoolean(booleanHeader);
      boolean booleanIdentifierAnIntP = Boolean.parseBoolean(booleanIdentifierAnInt);
      // create a new searcher with passed in arguments and perform a search
      Search search =
          new Search(value, booleanHeaderP, identifier, booleanIdentifierAnIntP, objectList);
      search.searches();
      // retrieve the data that was returned from the search
      List<List<String>> dataList = search.getTestList();
      responseMap.put("data", dataList);
      return new SearchSuccessResponse(responseMap).serialize();
    } catch (Exception e) {
      e.printStackTrace();
      // Exception stack trace printed out
    }
    return new SearchFailureResponse().serialize();
  }

  /** Response object to send with the information that was requested in the search */
  public record SearchSuccessResponse(String response_type, Map<String, Object> responseMap) {
    public SearchSuccessResponse(Map<String, Object> responseMap) {
      this("success", responseMap);
    }
    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      try {
        // Initialize Moshi which takes in this class and returns it as JSON!
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<SearchSuccessResponse> adapter = moshi.adapter(SearchSuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        // For debugging purposes, show in the console _why_ this fails
        // Otherwise we'll just get an error 500 from the API in integration
        // testing.
        e.printStackTrace();
        throw e;
      }
    }
  }

  /** Response object to send if there was an error */
  public record SearchFailureResponse(String response_type) {
    public SearchFailureResponse() {
      this("error: was not able to perform search");
    }

    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(SearchFailureResponse.class).toJson(this);
    }
  }
}

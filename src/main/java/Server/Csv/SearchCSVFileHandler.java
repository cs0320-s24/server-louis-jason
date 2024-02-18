package Server.Csv;

import Searcher.Search;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.ArrayList;
import java.util.List;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This is the SearchCSVHandler class. This class is to handle any requests made to the searchcsv
 * path in the server class.
 */
public class SearchCSVFileHandler implements Route {

  private CSVDataWrapper<List<String>> data;

  /**
   * This is the constructor of the SearchCSVFileHandler. This is where we set up the instance of
   * Datawrapper.
   *
   * @param data
   */
  public SearchCSVFileHandler(CSVDataWrapper<List<String>> data) {
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
    String booleanIdentifierAnInt = request.queryParams("booleanIdentifierAnInt");
    String identifier = request.queryParams("identifier");

    // Create params list to echo back
    List<String> params = new ArrayList<String>();
    params.add(value);
    params.add(booleanHeader);
    params.add(booleanIdentifierAnInt);
    params.add(identifier);

    try {
      // get the parsed list from the datawrapper
      List<List<String>> objectList = this.data.parseCSV();
      // convert the necessary string arguments into booleans
      boolean booleanHeaderP = Boolean.parseBoolean(booleanHeader);
      boolean booleanIdentifierAnIntP = Boolean.parseBoolean(booleanIdentifierAnInt);
      // create a new searcher with passed in arguments and perform a search
      Search search =
          new Search(value, booleanHeaderP, identifier, booleanIdentifierAnIntP, objectList);
      // retrieve the data that was returned from the search
      List<List<String>> dataList = search.searches();
      return new SearchSuccessResponse(dataList, params).serialize();
    } catch (Exception e) {
      e.printStackTrace();
      // Exception stack trace printed out
      if (this.data.nullParser()) {
        return new SearchFailureResponse("error_datasource", "Please load a file first")
            .serialize();
      }
      if (e instanceof NullPointerException) {
        String failure =
            "Error handling parameters. "
                + "Usage: searchcsv?value=<term> "
                + "Optional parameters: booleanHeader=true/false, "
                + "booleanHeaderAnInt=true/false, "
                + "identifier=<name or integer depending on previous selection>";
        return new SearchFailureResponse("error_bad_request", failure).serialize();
      }
      String failure = e.getMessage();
      return new SearchFailureResponse("error_datasource", failure).serialize();
    }
  }

  /** Response object to send with the information that was requested in the search */
  public record SearchSuccessResponse(
      String response_type, List<List<String>> data, List<String> params) {
    public SearchSuccessResponse(List<List<String>> data, List<String> params) {
      this("success", data, params);
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
  public record SearchFailureResponse(String result, String message) {
    public SearchFailureResponse(String result, String message) {
      this.result = result;
      this.message = message;
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

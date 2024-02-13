package Server;

import Parser.CSVParse;
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
 * This class is used to illustrate how to build and send a GET request then prints the response. It
 * will also demonstrate a simple Moshi deserialization from online data.
 */
// TODO 1: Check out this Handler. How can we make it only get activities based on participant #?
// See Documentation here: https://www.boredapi.com/documentation
public class SearchCSVFileHandler implements Route {
  /**
   * This handle method needs to be filled by any class implementing Route. When the path set in
   * edu.brown.cs.examples.moshiExample.server.Server gets accessed, it will fire the handle method.
   *
   * <p>NOTE: beware this "return Object" and "throws Exception" idiom. We need to follow it because
   * the library uses it, but in general this lowers the protection of the type system.
   *
   * @param request The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   */
  private DataWrapper<List<String>> data;

  public SearchCSVFileHandler(DataWrapper<List<String>> data) {
    this.data = data;
  }

  @Override
  public Object handle(Request request, Response response) {
    // If you are interested in how parameters are received, try commenting out and
    // printing these lines! Notice that requesting a specific parameter requires that parameter
    // to be fulfilled.
    // If you specify a queryParam, you can access it by appending ?parameterName=name to the
    // endpoint
    // ex. http://localhost:3232/activity?participants=num
    //    Set<String> params = request.queryParams();
    //     System.out.println(params);
    String value = request.queryParams("value");
    String booleanHeader = request.queryParams("booleanHeader");
    String identifier = request.queryParams("identifier");
    String booleanIdentifierAnInt = request.queryParams("booleanIdentifierAnInt");
    //     System.out.println(participants);

    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();
    try {
      CSVParse<List<String>> parser = this.data.getCSVParser();
      List<List<String>> objectList = parser.parse();
      // NEED TO ERROR CHECK BELOW
      boolean booleanHeaderP = Boolean.parseBoolean(booleanHeader);
      boolean booleanIdentifierAnIntP = Boolean.parseBoolean(booleanIdentifierAnInt);

      Search search =
          new Search(value, booleanHeaderP, identifier, booleanIdentifierAnIntP, objectList);
      search.searches();
      List<List<String>> dataList = search.getTestList();
      responseMap.put("data", dataList);
      return new SearchSuccessResponse(responseMap).serialize();
    } catch (Exception e) {
      e.printStackTrace();
      // This is a relatively unhelpful exception message. An important part of this sprint will be
      // in learning to debug correctly by creating your own informative error messages where Spark
      // falls short.
      //      responseMap.put("result", "Exception");
    }
    return new SearchFailureResponse().serialize();
  }

  /** Response object to send, containing a soup with certain ingredients in it */
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

  /** Response object to send if someone requested soup from an empty Menu */
  public record SearchFailureResponse(String response_type) {
    public SearchFailureResponse() {
      this("error");
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

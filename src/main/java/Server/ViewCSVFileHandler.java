package Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This is the ViewCSVHandler class. This class is to handle any requests made to the viewcsv path
 * in the server class.
 */
public class ViewCSVFileHandler implements Route {
  private DataWrapper<List<String>> data;

  /**
   * This is the constructor of the ViewCSVFileHandler. This is where we set up the instance of
   * Datawrapper.
   *
   * @param data
   */
  public ViewCSVFileHandler(DataWrapper<List<String>> data) {
    this.data = data;
  }

  /**
   * This handle method needs to be filled by any class implementing Route. When the path set in
   * Server gets accessed, it will fire the handle method. This handle method is used for viewing a
   * CSV file that was loaded in.
   *
   * @param request The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   */
  @Override
  public Object handle(Request request, Response response) {

    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // get parsed data from the loaded in CSVFile
      List<List<String>> objectList = this.data.parseCSV();
      // Adds results to the responseMap
      responseMap.put("data", objectList);
      // serialize the responseMap
      return new ParseSuccessResponse(responseMap).serialize();
    } catch (Exception e) {
      e.printStackTrace();
      // Error printed and its stack trace
    }
    return new ParseFailureResponse().serialize();
  }
  /** Response object to send, containing the CSVFile to view */
  public record ParseSuccessResponse(String response_type, Map<String, Object> responseMap) {
    public ParseSuccessResponse(Map<String, Object> responseMap) {
      this("success", responseMap);
    }
    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      try {
        // Initialize Moshi which takes in this class and returns it as JSON!
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<ParseSuccessResponse> adapter = moshi.adapter(ParseSuccessResponse.class);
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
  public record ParseFailureResponse(String response_type) {
    public ParseFailureResponse() {
      this("error: unable to view CSVFile");
    }

    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(ParseFailureResponse.class).toJson(this);
    }
  }
}

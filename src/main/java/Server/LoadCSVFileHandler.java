package Server;

import Creator.Creator;
import Parser.CSVParse;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This class is used to illustrate how to build and send a GET request then prints the response. It
 * will also demonstrate a simple Moshi deserialization from online data.
 */
// TODO 1: Check out this Handler. How can we make it only get activities based on participant #?
// See Documentation here: https://www.boredapi.com/documentation
public class LoadCSVFileHandler implements Route {
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

  public LoadCSVFileHandler(DataWrapper<List<String>> data) {
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
    Set<String> params = request.queryParams();
    //     System.out.println(params);
    String path = request.queryParams("path");
    //     System.out.println(path);
    String fileLocation = "data/" + path;

    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // Sends a request to the API and receives JSON back
      // Make this work with loading a CSVfile from a path+name
      CSVParse<List<String>> csvfileParser = this.sendRequest(fileLocation);
      // Deserializes JSON into an CSVFile
      // CSVFile csvfile = CSVFileAPIUtilities.deserializeCSVFile(csvfileJson);
      // Adds results to the responseMap
      responseMap.put("result", "success");
      this.data.setCSVParser(csvfileParser);
      return responseMap;
    } catch (Exception e) {
      e.printStackTrace();
      // This is a relatively unhelpful exception message. An important part of this sprint will be
      // in learning to debug correctly by creating your own informative error messages where Spark
      // falls short.
      responseMap.put("result", "Exception");
    }
    return responseMap;
  }

  private CSVParse<List<String>> sendRequest(String fileLocation)
      throws URISyntaxException, IOException, InterruptedException {
    // Build a request to this BoredAPI. Try out this link in your browser, what do you see?
    // TODO 1: Looking at the documentation, how can we add to the URI to query based
    // on participant number?
    FileReader file = new FileReader(fileLocation);
    Creator creator = new Creator();
    CSVParse<List<String>> parser = new CSVParse<>(file, creator);

    // What's the difference between these two lines? Why do we return the body? What is useful from
    // the raw response (hint: how can we use the status of response)?
    System.out.println("Loaded file at " + fileLocation);

    return parser;
  }
}
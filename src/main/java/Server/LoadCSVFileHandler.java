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

    String path = request.queryParams("path");
    String fileLocation = "data/" + path;

    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // Sends a request to the API and receives JSON back
      CSVParse<List<String>> csvFileParser = this.sendRequest(fileLocation);
      // Adds results to the responseMap
      responseMap.put("result", "success");
      this.data.setCSVParser(csvFileParser);
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
      throws IOException{

    FileReader file = new FileReader(fileLocation);
    Creator creator = new Creator();
    CSVParse<List<String>> parser = new CSVParse<>(file, creator);

    System.out.println("Loaded file at " + fileLocation);

    return parser;
  }
}

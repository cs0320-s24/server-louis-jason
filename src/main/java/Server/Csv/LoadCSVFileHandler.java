package Server.Csv;

import Creator.Creator;
import Parser.CSVParse;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This is the LoadCSVHandler class. This class is to handle any requests made to the loadcsv path
 * in the server class.
 */
public class LoadCSVFileHandler implements Route {
  /**
   * This handle method needs to be filled by any class implementing Route. When the path set in
   * Server gets accessed, it will fire the handle method. This handle method is used for loading a
   * CSV file that is in the data folder.
   *
   * @param request The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   */
  private CSVDataWrapper<List<String>> data;

  /**
   * This is the constructor of the LoadCSVFileHandler. This is where we set up the instance of
   * Datawrapper.
   *
   * @param data
   */
  public LoadCSVFileHandler(CSVDataWrapper<List<String>> data) {
    this.data = data;
  }

  /**
   * This method gets the path requested by the user and attempts to load in the requested CSVFile.
   *
   * @param request
   * @param response
   * @return
   */
  @Override
  public Object handle(Request request, Response response) {

    // requests passed in argument
    String path = request.queryParams("path");
    String fileLocation = "data/" + path;

    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();

    if (path == null) {
      responseMap.put("result", "error_bad_json");
      responseMap.put("message", "Instructions: loadcsv?path=<pathname>");
      return adapter.toJson(responseMap);
    }
    try {
      // Calls helper method that attempts to open the CSV file and create a parser
      CSVParse<List<String>> csvFileParser = this.sendRequest(fileLocation);
      // Adds results to the responseMap
      responseMap.put("result", "success");
      // Set the CSVParser to the current loaded in files parser
      this.data.setCSVParser(csvFileParser);
      return adapter.toJson(responseMap);
    } catch (Exception e) {
      e.printStackTrace();
      // Exception message if unable to load it.
      responseMap.put("result", "error_datasource");
      responseMap.put("message", "Cannot find specified file. Make sure path is within data/ directory.");
    }
    return adapter.toJson(responseMap);
  }

  /**
   * Helper method that is used to set up the CSV file and the parser used to parse the file.
   *
   * @param fileLocation
   * @return
   * @throws IOException
   */
  private CSVParse<List<String>> sendRequest(String fileLocation) throws IOException {

    FileReader file = new FileReader(fileLocation);
    Creator creator = new Creator();
    CSVParse<List<String>> parser = new CSVParse<>(file, creator);

    return parser;
  }
}

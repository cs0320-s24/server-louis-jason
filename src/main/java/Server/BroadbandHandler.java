package Server;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This class is used to illustrate how to build and send a GET request then prints the response. It
 * will also demonstrate a simple Moshi deserialization from online data.
 */
// TODO 1: Check out this Handler. How can we make it only get activities based on participant #?
// See Documentation here: https://www.boredapi.com/documentation
public class BroadbandHandler implements Route {
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
  private HashMap<String, String> stateCodeMap;

  public BroadbandHandler() {
    try {
      this.stateCodeMap = getStateCodes();
    } catch (Exception e) {
      e.printStackTrace();
      this.stateCodeMap = new HashMap<>();
    }
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
    String county = request.queryParams("county");
    String state = request.queryParams("state");
    //     System.out.println(path);

    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // Sends a request to the API and receives JSON back
      // Make this work with loading a CSVfile from a path+name
      if (state.contains("%20")) {
        String[] stateArr = state.split("%20");
        state = stateArr[0] + " " + stateArr[1];
      }
      if (county.contains("%20")) {
        String[] countyArr = county.split("%20");
        String countyPlace = "";
        for (String string : countyArr) {
          countyPlace = countyPlace + string;
        }
        county = countyPlace;
      }
      String stateCode = this.stateCodeMap.get(state);
      HashMap<String, String> countyNumberMap =
          BroadbandAPIUtilities.deserializeBroadbandCounty(sendCountyRequest(stateCode));

      //      String countyCode = "*";
      //      for (CountyNumberResponse countyNumberResponse : countyNumberList) {
      //        if (countyNumberResponse.NAME.contains(county)) {
      //          countyCode = countyNumberResponse.county;
      //          break;
      //        }
      //      }
      String countyCode = countyNumberMap.get(county);
      // WE WILL HAVE TO DESERIALIZE AND SERIALIZE THE BROADBANDDATA BELOW
      String broadbandData = this.sendRequest(countyCode, stateCode);
      // Deserializes JSON into an CSVFile
      // CSVFile csvfile = CSVFileAPIUtilities.deserializeCSVFile(csvfileJson);
      // Adds results to the responseMap
      responseMap.put("result", "success");
      responseMap.put("broadband", broadbandData);
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

  private String sendRequest(String county, String state)
      throws URISyntaxException, IOException, InterruptedException {
    // Build a request to this BoredAPI. Try out this link in your browser, what do you see?
    // TODO 1: Looking at the documentation, how can we add to the URI to query based
    // on participant number?
    HttpRequest buildCensusRequest =
        HttpRequest.newBuilder()
            .uri(
                new URI(
                    "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
                        + county
                        + "&in=state:"
                        + state))
            .GET()
            .build();

    // Send that API request then store the response in this variable. Note the generic type.
    HttpResponse<String> sentCensusResponse =
        HttpClient.newBuilder()
            .build()
            .send(buildCensusRequest, HttpResponse.BodyHandlers.ofString());

    // What's the difference between these two lines? Why do we return the body? What is useful from
    // the raw response (hint: how can we use the status of response)?
    //    System.out.println(sentCensusResponse);
    //    System.out.println(sentCensusResponse.body());

    return sentCensusResponse.body();
  }

  private String sendCountyRequest(String state)
      throws URISyntaxException, IOException, InterruptedException {
    // Build a request to this BoredAPI. Try out this link in your browser, what do you see?
    // TODO 1: Looking at the documentation, how can we add to the URI to query based
    // on participant number?
    HttpRequest retrieveCountyNums =
        HttpRequest.newBuilder()
            .uri(
                new URI(
                    "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:"
                        + state))
            .GET()
            .build();

    // Send that API request then store the response in this variable. Note the generic type.
    HttpResponse<String> countyNums =
        HttpClient.newBuilder()
            .build()
            .send(retrieveCountyNums, HttpResponse.BodyHandlers.ofString());

    // What's the difference between these two lines? Why do we return the body? What is useful from
    // the raw response (hint: how can we use the status of response)?
    //    System.out.println(sentCensusResponse);
    //    System.out.println(sentCensusResponse.body());

    return countyNums.body();
  }

  private HashMap<String, String> getStateCodes()
      throws URISyntaxException, IOException, InterruptedException {
    // Build a request to this BoredAPI. Try out this link in your browser, what do you see?
    // TODO 1: Looking at the documentation, how can we add to the URI to query based
    // on participant number?
    HttpRequest retrieveStateNums =
        HttpRequest.newBuilder()
            .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*"))
            .GET()
            .build();

    // Send that API request then store the response in this variable. Note the generic type.
    HttpResponse<String> stateNums =
        HttpClient.newBuilder()
            .build()
            .send(retrieveStateNums, HttpResponse.BodyHandlers.ofString());

    // What's the difference between these two lines? Why do we return the body? What is useful from
    // the raw response (hint: how can we use the status of response)?
    //    System.out.println(sentBoredApiResponse);
    //    System.out.println(sentBoredApiResponse.body());
    HashMap<String, String> stateCodesMap =
        BroadbandAPIUtilities.deserializeBroadband(stateNums.body());
    return stateCodesMap;
  }

  //  public static List<CountyNumberResponse> deserializeCountyNumber(String jsonList)
  //      throws IOException {
  //    List<CountyNumberResponse> deserializedCountyNumbers = new ArrayList<>();
  //    System.out.println(jsonList);
  //    try {
  //      Moshi moshi = new Moshi.Builder().build();
  //      // notice the type and JSONAdapter parameterized type match the return type of the method
  //      // Since List is generic, we shouldn't just pass List.class to the adapter factory.
  //      // Instead, let's be more precise. Java has built-in classes for talking about generic
  // types
  //      // programmatically.
  //      // Building libraries that use them is outside the scope of this class, but we'll follow
  // the
  //      // Moshi docs'
  //      // template by creating a Type object corresponding to List<Ingredient>:
  //      Type listType = Types.newParameterizedType(List.class, CountyNumberResponse.class);
  //      JsonAdapter<List<CountyNumberResponse>> adapter = moshi.adapter(listType);
  //
  //      deserializedCountyNumbers = adapter.fromJson(jsonList);
  //
  //      return deserializedCountyNumbers;
  //    }
  //    // From the Moshi Docs (https://github.com/square/moshi):
  //    //   "Moshi always throws a standard java.io.IOException if there is an error reading the
  // JSON
  //    // document, or if it is malformed. It throws a JsonDataException if the JSON document is
  //    // well-formed, but doesn't match the expected format."
  //    catch (IOException e) {
  //      // In a real system, we wouldn't println like this, but it's useful for demonstration:
  //      System.err.println("OrderHandler: string wasn't valid JSON.");
  //      throw e;
  //    } catch (JsonDataException e) {
  //      // In a real system, we wouldn't println like this, but it's useful for demonstration:
  //      System.err.println("OrderHandler: JSON wasn't in the right format.");
  //      throw e;
  //    }
  //  }

  /** Response object to send, containing a soup with certain ingredients in it */
  public record CountyNumberResponse(String NAME, String state, String county) {}
}

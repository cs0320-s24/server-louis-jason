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
  private BroadbandInterface<String,String> cachedBroadbandSearcher;

  public BroadbandHandler(BroadbandInterface<String,String> cachedBroadbandSearcher) {
    this.cachedBroadbandSearcher = cachedBroadbandSearcher;
    try {
      this.stateCodeMap = getStateCodes();
    } catch (Exception e) {
      e.printStackTrace();
      this.stateCodeMap = new HashMap<>();
    }
  }

  @Override
  public Object handle(Request request, Response response) {
    String county = request.queryParams("county");
    String state = request.queryParams("state");
    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();
    try {
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

      //code below should also probably be cached since also accessing API
      HashMap<String, String> countyNumberMap =
          BroadbandAPIUtilities.deserializeBroadbandCounty(sendCountyRequest(stateCode));
      String countyCode = countyNumberMap.get(county);

      // WE WILL HAVE TO DESERIALIZE AND SERIALIZE THE BROADBANDDATA BELOW
      String countyAndStateCode = countyCode + "," + stateCode;
      String broadbandData = this.cachedBroadbandSearcher.search(countyAndStateCode);
      //String broadbandData = this.sendRequest(countyCode, stateCode);

      List<List<String>> deserializedBroadbandData = BroadbandAPIUtilities.deserializeBroadbandData(broadbandData);
      // Adds results to the responseMap
      responseMap.put("result", "success");
      responseMap.put("state", state);
      //do we need to worry if they put a * as the county? if so
      //below is not good code cause what if they put in a * for county then there would be multiple broadbands and multiple counties
      responseMap.put("county", county);
      responseMap.put("broadband", deserializedBroadbandData.get(1).get(1));
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

//  private String sendRequest(String county, String state)
//      throws URISyntaxException, IOException, InterruptedException {
//    HttpRequest buildCensusRequest =
//        HttpRequest.newBuilder()
//            .uri(
//                new URI(
//                    "https://api.census.gov/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:"
//                        + county
//                        + "&in=state:"
//                        + state))
//            .GET()
//            .build();
//
//    // Send that API request then store the response in this variable. Note the generic type.
//    HttpResponse<String> sentCensusResponse =
//        HttpClient.newBuilder()
//            .build()
//            .send(buildCensusRequest, HttpResponse.BodyHandlers.ofString());
//
//    return sentCensusResponse.body();
//  }

  private String sendCountyRequest(String state)
      throws URISyntaxException, IOException, InterruptedException {

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

    return countyNums.body();
  }

  private HashMap<String, String> getStateCodes()
      throws URISyntaxException, IOException, InterruptedException {

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

    HashMap<String, String> stateCodesMap =
        BroadbandAPIUtilities.deserializeBroadband(stateNums.body());
    return stateCodesMap;
  }
}

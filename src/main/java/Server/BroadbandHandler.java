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
  private BroadbandInterface<String, BroadbandInfo> cachedBroadbandSearcher;

  private BroadbandInfo broadbandInfo;

  public BroadbandHandler(BroadbandInterface<String,BroadbandInfo> cachedBroadbandSearcher) {
    this.cachedBroadbandSearcher = cachedBroadbandSearcher;
  }

  @Override
  public Object handle(Request request, Response response) {
    String countyName = request.queryParams("county");
    String stateName = request.queryParams("state");
    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();
    try {
      if (stateName.contains("%20")) {
        String[] stateArr = stateName.split("%20");
        stateName = stateArr[0] + " " + stateArr[1];
      }
      if (countyName.contains("%20")) {
        String[] countyArr = countyName.split("%20");
        StringBuilder countyPlace = new StringBuilder();
        for (String string : countyArr) {
          countyPlace.append(" ").append(string);
        }
        countyName = countyPlace.toString().trim();
      }

      BroadbandInfo broadbandToSearch = BroadbandAPIUtilities.makeBroadbandInfo(countyName, stateName);
      //code below should also probably be cached since also accessing API
      String broadbandResult = this.cachedBroadbandSearcher.search(broadbandToSearch);
      System.out.println(broadbandResult);
      List<List<String>> deserializedBroadbandData = BroadbandAPIUtilities.deserializeBroadbandData(broadbandResult);
      // Adds results to the responseMap
      if (deserializedBroadbandData.isEmpty()){
        responseMap.put("result", "Exception: error_datasource");
      }
      else {
        responseMap.put("result", "success");
        if (countyName.equals("*")){
          List<List<String>> broadBandDataReturn = new ArrayList<>();
          for (int i = 1; i<deserializedBroadbandData.size(); i++){
            List<String> oneCountyData = new ArrayList<>();
            oneCountyData.add(deserializedBroadbandData.get(i).get(0));
            oneCountyData.add(deserializedBroadbandData.get(i).get(1));
            broadBandDataReturn.add(oneCountyData);
          }
          responseMap.put("broadband data", broadBandDataReturn);
        }
        else {
          responseMap.put("broadband data", deserializedBroadbandData.get(1).get(1));
        }
      }
      //do we need to worry if they put a * as the county? if so
      //below is not good code cause what if they put in a * for county then there would be multiple broadbands and multiple counties
      responseMap.put("state entered", stateName);
      responseMap.put("county entered", countyName);
      responseMap.put("date data was retrieved on", java.time.LocalDateTime.now());
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

//  private String sendCountyRequest(String state)
//      throws URISyntaxException, IOException, InterruptedException {
//
//    HttpRequest retrieveCountyNums =
//        HttpRequest.newBuilder()
//            .uri(
//                new URI(
//                    "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:"
//                        + state))
//            .GET()
//            .build();
//
//    // Send that API request then store the response in this variable. Note the generic type.
//    HttpResponse<String> countyNums =
//        HttpClient.newBuilder()
//            .build()
//            .send(retrieveCountyNums, HttpResponse.BodyHandlers.ofString());
//
//    return countyNums.body();
//  }

//  private HashMap<String, String> getStateCodes()
//      throws URISyntaxException, IOException, InterruptedException {
//
//    HttpRequest retrieveStateNums =
//        HttpRequest.newBuilder()
//            .uri(new URI("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*"))
//            .GET()
//            .build();
//
//    // Send that API request then store the response in this variable. Note the generic type.
//    HttpResponse<String> stateNums =
//        HttpClient.newBuilder()
//            .build()
//            .send(retrieveStateNums, HttpResponse.BodyHandlers.ofString());
//
//    HashMap<String, String> stateCodesMap =
//        BroadbandAPIUtilities.deserializeBroadband(stateNums.body());
//    return stateCodesMap;
//  }
}

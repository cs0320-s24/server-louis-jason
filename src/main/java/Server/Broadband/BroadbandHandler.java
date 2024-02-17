package Server.Broadband;

import JsonTypes.BroadbandInfo;
import Server.Cache.SearchInterface;

import java.lang.reflect.Type;
import java.util.*;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * This is the BroadbandHandler class. This class is to handle any requests made to the broadband
 * path in the server class.
 */
public class BroadbandHandler implements Route {
  /**
   * This handle method needs to be filled by any class implementing Route. When the path set in
   * Server gets accessed, it will fire the handle method. This handle method is used for retrieving
   * data from the Census API and returning only the broadband data to our proxy API. Caching is
   * also set up to be handled in this handler.
   *
   * @param request The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   */
  private SearchInterface<String, BroadbandInfo> broadbandSearcher;

  /**
   * Constructor takes in an instance of BroadbandInterface, used for caching
   *
   * @param cachedBroadbandSearcher
   */
  public BroadbandHandler(SearchInterface<String, BroadbandInfo> cachedBroadbandSearcher) {
    this.broadbandSearcher = cachedBroadbandSearcher;
  }

  /**
   * This is our handle method which was described above. If bad requests are passed in or it is not
   * able to retrieve the data, then it will return a helpful error message. Otherwise, it will
   * retrun the data requested.
   *
   * @param request
   * @param response
   * @return
   */
  @Override
  public Object handle(Request request, Response response) {
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    // request arguments passed in
    String countyName = request.queryParams("county");
    String stateName = request.queryParams("state");
    // Creates a hashmap to store the results of the request
    Map<String, Object> responseMap = new HashMap<>();
    try {
      // below is in case the %20 is passed in from the URL, if so we will parse this out
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
      // Create an instance of the BroadbandInfo class so we can search for it in the census API
      BroadbandInfo broadbandToSearch =
          BroadbandAPIUtilities.makeBroadbandInfo(countyName, stateName);
      // look for information in the cache, if it is not in the cache then it will do a regular
      // search call
      String broadbandResult = this.broadbandSearcher.search(broadbandToSearch);
      // deserialize the data that was returned from our search call
      List<List<String>> deserializedBroadbandData =
          BroadbandAPIUtilities.deserializeBroadbandData(broadbandResult);
      // if a state or county that does not exist was entered then below exception is thrown
      if (broadbandResult.equals("error_bad_request")) {
        responseMap.put("result", "error_bad_request");
      }
      // if the data did not exist in the census API then below error is thrown
      else if (deserializedBroadbandData.isEmpty()) {
        responseMap.put("result", "error_datasource");
      }
      // if it worked then below code happens
      else {
        responseMap.put("result", "success");
        // if user passed in the * as county name, then there will be multiple responses, so have to
        // format it so that it is usable by the user
        if (countyName.equals("*")) {
          List<List<String>> broadBandDataReturn = new ArrayList<>();
          for (int i = 1; i < deserializedBroadbandData.size(); i++) {
            List<String> oneCountyData = new ArrayList<>();
            oneCountyData.add(deserializedBroadbandData.get(i).get(0));
            oneCountyData.add(deserializedBroadbandData.get(i).get(1));
            broadBandDataReturn.add(oneCountyData);
          }
          // put data into the responseMap
          responseMap.put("broadband_data", broadBandDataReturn);
        } else {
          // this occurs if * was not passed in as county name
          responseMap.put("broadband_data", deserializedBroadbandData.get(1).get(1));
        }
      }
      // put in response map arguments and time retrieved so that the user can debug on their end if
      // needed
      responseMap.put("state_entered", stateName);
      responseMap.put("county_entered", countyName);
      responseMap.put("date_of_retrieval", java.time.LocalDateTime.now().toString());
      return adapter.toJson(responseMap);
    } catch (Exception e) {
      e.printStackTrace();
      // if gets to this point then returns exception of error_bad_json
      responseMap.put("result", "Exception: error_bad_json");
    }
    return adapter.toJson(responseMap);
  }
}

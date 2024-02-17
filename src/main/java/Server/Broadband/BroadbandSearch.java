package Server.Broadband;

import JsonTypes.BroadbandInfo;
import Server.Cache.SearchInterface;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

/**
 * This class is the BroadbandSearch class. It is used whenever we need to search and retrieve
 * information from the census API. It implements the BroadbandInterface so that caching can be set
 * up. This class requests for the state codes, county codes, and broadband data. The states codes
 * are only requested for once in the constructor. The other are requested if it is not in the cache
 * already.
 */
public class BroadbandSearch implements SearchInterface<String, BroadbandInfo> {

  // Hashmap for the statecodes and its respective states
  private final HashMap<String, String> stateCodesMap;

  /**
   * Constructor for the BroadbandSearch. In the constructor the stateCodes hashmap is set up once
   * so that multiple requests are not made for the same information.
   *
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
   */
  public BroadbandSearch() throws URISyntaxException, IOException, InterruptedException {
    this.stateCodesMap = BroadbandSearch.getStateCodes();
  }

  /**
   * Method search that is implemented from the interface. It takes in a broadbandinfo class. It
   * uses this to get the countyname and statename. From there we get the respective codes if
   * possible and send a request to the census API to get the broadband data for that respective
   * county and state.
   *
   * @param broadbandInfo
   * @return
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
   */
  @Override
  public String search(BroadbandInfo broadbandInfo)
      throws URISyntaxException, IOException, InterruptedException {
    // Getting the county name and state name that was passed via the BroadbandInfo instance
    String countyName = broadbandInfo.getCountyName();
    String stateName = broadbandInfo.getStateName();
    // set up the string that will be returned
    String broadbandData;

    String stateCode = this.stateCodesMap.get(stateName);
    // if state does not exist then send bad request error
    if (stateCode == null) {
      broadbandData = "error_bad_request";
      return broadbandData;
    }
    // if county code is equal to * then don't need to send a request to census API to get the
    // county code
    String countyCode;
    if (countyName.equals("*")) {
      countyCode = "*";
    } else {
      // ask the API for the code for the county name that was passed in
      countyCode = this.getCountyCodes(stateCode).get(countyName);
      // if the county does not exist in the census API then return a bad request error
      if (countyCode == null) {
        broadbandData = "error_bad_request";
        return broadbandData;
      }
    }
    try {
      // request for broadband data from the census API passing in the states and county codes we
      // found above
      broadbandData = this.sendRequest(countyCode, stateCode);
    } catch (Exception e) {
      e.printStackTrace();
      broadbandData = "Error retrieving data";
    }
    // Adds results to the responseMap by returning
    return broadbandData;
  }

  /**
   * This method is what sends the request for broadband information from the census API. It does
   * this by requesting the information from the url below. We then return the JSON of information
   * that we retrieved from the census API.
   *
   * @param county
   * @param state
   * @return
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
   */
  private String sendRequest(String county, String state)
      throws URISyntaxException, IOException, InterruptedException {
    // setting up the request to get information from the below URL
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

    return sentCensusResponse.body();
  }

  /**
   * This method is what sends the request for state code information from the census API. It does
   * this by requesting the information from the url below. We then deserialize the JSON it returns
   * and turn it into a Hashmap. This hashmap is then returned.
   *
   * @return
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
   */
  private static HashMap<String, String> getStateCodes()
      throws URISyntaxException, IOException, InterruptedException {
    // setting up the request to get information from the below URL
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
    // Deserialize the JSON that was returned into a Hashmap.
    HashMap<String, String> stateCodesMap =
        BroadbandAPIUtilities.deserializeBroadbandIntoStateMap(stateNums.body());
    return stateCodesMap;
  }

  /**
   * This method is what sends the request for county code information from the census API. It does
   * this by requesting the information from the url below. It then gets the JSON from the census
   * API and returns that JSON.
   *
   * @param stateCode
   * @return
   * @throws URISyntaxException
   * @throws IOException
   * @throws InterruptedException
   */
  private HashMap<String, String> getCountyCodes(String stateCode)
      throws URISyntaxException, IOException, InterruptedException {
    // setting up the request to get information from the below URL
    HttpRequest retrieveCountyNums =
        HttpRequest.newBuilder()
            .uri(
                new URI(
                    "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:"
                        + stateCode))
            .GET()
            .build();

    // Send that API request then store the response in this variable. Note the generic type.
    HttpResponse<String> countyNums =
        HttpClient.newBuilder()
            .build()
            .send(retrieveCountyNums, HttpResponse.BodyHandlers.ofString());

    HashMap<String, String> countyCodesMap =
        BroadbandAPIUtilities.deserializeBroadbandIntoStateMap(countyNums.body());

    return countyCodesMap;
  }
}

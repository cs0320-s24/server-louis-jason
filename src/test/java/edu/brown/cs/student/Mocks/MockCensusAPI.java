package edu.brown.cs.student.Mocks;

import JsonTypes.BroadbandInfo;
import Server.Broadband.BroadbandAPIUtilities;
import Server.Cache.SearchInterface;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class MockCensusAPI implements SearchInterface<String, BroadbandInfo> {
  // Hashmap for the statecodes and its respective states
  private final HashMap<String, String> stateCodesMap;

  /** Adds in some mock values for stateCodesMap */
  public MockCensusAPI() throws URISyntaxException, IOException, InterruptedException {
    this.stateCodesMap = new HashMap<String, String>();
    this.stateCodesMap.put("California", "06");
    this.stateCodesMap.put("Alabama", "01");
    this.stateCodesMap.put("Texas", "48");
  }

  /**
   * Method search that is implemented from the interface. It takes in a broadbandinfo class. It
   * uses this to get the countyname and statename. From there we get the respective codes if
   * possible and send a request to the census API to get the broadband data for that respective
   * county and state.
   *
   * @param broadbandInfo
   * @return
   */
  public String search(BroadbandInfo broadbandInfo) throws IOException {
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
  private String sendRequest(String county, String state) throws Exception {
    // Sends back correct information if you search for Marin County in California
    // Throws an exception otherwise
    // ["Marin County, California","94.0","06","041"]

    String content =
        new String(
            Files.readAllBytes(
                Paths.get("src/test/java/edu/brown/cs/student/Mocks/MarinCounty.txt")));

    if (county.equals("041") && state.equals("06")) {
      return content;
    } else {
      throw new IOException();
    }
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
  private HashMap<String, String> getCountyCodes(String stateCode) throws IOException {
    // Only returns if given California code
    String content =
        new String(
            Files.readAllBytes(
                Paths.get("src/test/java/edu/brown/cs/student/Mocks/CaliCountyCodes.txt")));

    if (stateCode.equals("06")) {
      HashMap<String, String> countyCodesMap =
          BroadbandAPIUtilities.deserializeBroadbandIntoStateMap(content);
      return countyCodesMap;
    } else {
      throw new IOException();
    }
  }
}

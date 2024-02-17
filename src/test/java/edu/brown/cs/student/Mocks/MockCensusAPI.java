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
      broadbandData = "error_datasource";
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

    String content = "[[\"NAME\",\"S2802_C03_022E\",\"state\",\"county\"],\n" +
            "[\"Marin County, California\",\"94.0\",\"06\",\"041\"]]";

    if (county.equals("041") && state.equals("06")) {
      return content;
    } else {
      throw new Exception();
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
    String content = "[[\"NAME\",\"state\",\"county\"],\n" +
            "[\"Colusa County, California\",\"06\",\"011\"],\n" +
            "[\"Butte County, California\",\"06\",\"007\"],\n" +
            "[\"Alameda County, California\",\"06\",\"001\"],\n" +
            "[\"Alpine County, California\",\"06\",\"003\"],\n" +
            "[\"Amador County, California\",\"06\",\"005\"],\n" +
            "[\"Calaveras County, California\",\"06\",\"009\"],\n" +
            "[\"Contra Costa County, California\",\"06\",\"013\"],\n" +
            "[\"Del Norte County, California\",\"06\",\"015\"],\n" +
            "[\"Kings County, California\",\"06\",\"031\"],\n" +
            "[\"Glenn County, California\",\"06\",\"021\"],\n" +
            "[\"Humboldt County, California\",\"06\",\"023\"],\n" +
            "[\"Imperial County, California\",\"06\",\"025\"],\n" +
            "[\"El Dorado County, California\",\"06\",\"017\"],\n" +
            "[\"Fresno County, California\",\"06\",\"019\"],\n" +
            "[\"Inyo County, California\",\"06\",\"027\"],\n" +
            "[\"Kern County, California\",\"06\",\"029\"],\n" +
            "[\"Mariposa County, California\",\"06\",\"043\"],\n" +
            "[\"Lake County, California\",\"06\",\"033\"],\n" +
            "[\"Lassen County, California\",\"06\",\"035\"],\n" +
            "[\"Los Angeles County, California\",\"06\",\"037\"],\n" +
            "[\"Madera County, California\",\"06\",\"039\"],\n" +
            "[\"Marin County, California\",\"06\",\"041\"],\n" +
            "[\"Orange County, California\",\"06\",\"059\"],\n" +
            "[\"Mendocino County, California\",\"06\",\"045\"],\n" +
            "[\"Merced County, California\",\"06\",\"047\"],\n" +
            "[\"Modoc County, California\",\"06\",\"049\"],\n" +
            "[\"Mono County, California\",\"06\",\"051\"],\n" +
            "[\"Monterey County, California\",\"06\",\"053\"],\n" +
            "[\"Napa County, California\",\"06\",\"055\"],\n" +
            "[\"Nevada County, California\",\"06\",\"057\"],\n" +
            "[\"San Bernardino County, California\",\"06\",\"071\"],\n" +
            "[\"Sacramento County, California\",\"06\",\"067\"],\n" +
            "[\"San Benito County, California\",\"06\",\"069\"],\n" +
            "[\"Placer County, California\",\"06\",\"061\"],\n" +
            "[\"Plumas County, California\",\"06\",\"063\"],\n" +
            "[\"Riverside County, California\",\"06\",\"065\"],\n" +
            "[\"San Joaquin County, California\",\"06\",\"077\"],\n" +
            "[\"San Diego County, California\",\"06\",\"073\"],\n" +
            "[\"San Francisco County, California\",\"06\",\"075\"],\n" +
            "[\"Siskiyou County, California\",\"06\",\"093\"],\n" +
            "[\"San Luis Obispo County, California\",\"06\",\"079\"],\n" +
            "[\"San Mateo County, California\",\"06\",\"081\"],\n" +
            "[\"Santa Barbara County, California\",\"06\",\"083\"],\n" +
            "[\"Santa Clara County, California\",\"06\",\"085\"],\n" +
            "[\"Santa Cruz County, California\",\"06\",\"087\"],\n" +
            "[\"Shasta County, California\",\"06\",\"089\"],\n" +
            "[\"Sierra County, California\",\"06\",\"091\"],\n" +
            "[\"Yuba County, California\",\"06\",\"115\"],\n" +
            "[\"Solano County, California\",\"06\",\"095\"],\n" +
            "[\"Sonoma County, California\",\"06\",\"097\"],\n" +
            "[\"Stanislaus County, California\",\"06\",\"099\"],\n" +
            "[\"Sutter County, California\",\"06\",\"101\"],\n" +
            "[\"Tehama County, California\",\"06\",\"103\"],\n" +
            "[\"Trinity County, California\",\"06\",\"105\"],\n" +
            "[\"Ventura County, California\",\"06\",\"111\"],\n" +
            "[\"Yolo County, California\",\"06\",\"113\"],\n" +
            "[\"Tulare County, California\",\"06\",\"107\"],\n" +
            "[\"Tuolumne County, California\",\"06\",\"109\"]]";

    if (stateCode.equals("06")) {
      HashMap<String, String> countyCodesMap =
          BroadbandAPIUtilities.deserializeBroadbandIntoCountyMap(content);
      return countyCodesMap;
    } else {
      throw new IOException();
    }
  }
}

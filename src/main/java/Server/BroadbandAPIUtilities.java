package Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class is used for deserializing JSON from the CensusAPI into an instance of BroadbandInfo or
 * into list of strings.
 */
public class BroadbandAPIUtilities {

  /**
   * This method is used for deserializing the json of states and its codes. We deserialize it into
   * a hashmap<string, string>. This method only gets called once when the server is started up.
   *
   * @param jsonBroadband
   * @return
   */
  public static HashMap<String, String> deserializeBroadbandIntoStateMap(String jsonBroadband) {
    try {
      // Initializes Moshi
      Moshi moshi = new Moshi.Builder().build();

      // Initializes an adapter to a List class then uses it to parse the JSON.
      JsonAdapter<List> adapter = moshi.adapter(List.class);
      List<List<String>> mapped = adapter.fromJson(jsonBroadband);
      // Setup hashmap and implement it
      HashMap<String, String> stateMap = new HashMap<>();
      for (int i = 0; i < mapped.size(); i++) {
        stateMap.put(mapped.get(i).get(0), mapped.get(i).get(1));
      }

      return stateMap;
    }
    // Returns a empty Hashmap if there is an error.
    catch (IOException e) {
      e.printStackTrace();
      return new HashMap<String, String>();
    }
  }

  /**
   * This method is used for deserializing the json of counties and its codes. We deserialize it
   * into a hashmap<string, string>. This method gets called whenever a new county is entered and is
   * not in the cache.
   *
   * @param jsonBroadband
   * @return
   */
  public static HashMap<String, String> deserializeBroadbandCounty(String jsonBroadband) {
    try {
      // Initializes Moshi
      Moshi moshi = new Moshi.Builder().build();

      // Initializes an adapter to an Activity class then uses it to parse the JSON.
      JsonAdapter<List> adapter = moshi.adapter(List.class);

      List<List<String>> mapped = adapter.fromJson(jsonBroadband);
      // Setup hashmap and implement it
      HashMap<String, String> countyMap = new HashMap<>();
      for (int i = 0; i < mapped.size(); i++) {
        String county = mapped.get(i).get(0);
        // below it returns county and state, but we just want the county so parse everything before
        // the comma
        if (county.contains(",")) {
          String[] countyArr = county.split(",");
          county = countyArr[0];
        }
        // put the actual county name into the hashmap
        countyMap.put(county, mapped.get(i).get(2));
      }
      return countyMap;
    }
    // Returns an empty Hashmap if there is an error
    catch (IOException e) {
      e.printStackTrace();
      return new HashMap<String, String>();
    }
  }

  /**
   * This method deserializes the broadband data json that is received from the census API. We turn
   * it into a list of list of strings which is returned back for further manipulation in the
   * BroadbandHandler class.
   *
   * @param jsonBroadband
   * @return
   */
  public static List<List<String>> deserializeBroadbandData(String jsonBroadband) {
    try {
      // Initializes Moshi
      Moshi moshi = new Moshi.Builder().build();

      // Initializes an adapter to a List class then uses it to parse the JSON.
      JsonAdapter<List> adapter = moshi.adapter(List.class);

      List<List<String>> mapped = adapter.fromJson(jsonBroadband);
      return mapped;
    }
    // Returns an empty ArrayList if there is an error
    catch (IOException e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  /**
   * This method takes in a county and state and makes a new instance of BroadbandInfo class
   *
   * @param county
   * @param state
   * @return
   */
  public static BroadbandInfo makeBroadbandInfo(String county, String state) {
    return new BroadbandInfo(county, state);
  }
}

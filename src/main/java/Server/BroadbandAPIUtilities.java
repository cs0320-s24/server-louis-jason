package Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class shows a possible implementation of deserializing JSON from the BoredAPI into a
 * CSVFile.
 */
public class BroadbandAPIUtilities {

  /**
   * Deserializes JSON from the BoredAPI into an CSVFile object.
   *
   * @param jsonBroadband
   * @return
   */
  public static BroadbandInfo deserializeBroadband(String jsonBroadband) {
    try {
      // Initializes Moshi
      Moshi moshi = new Moshi.Builder().build();

      // Initializes an adapter to an Activity class then uses it to parse the JSON.
      JsonAdapter<BroadbandInfo> adapter = moshi.adapter(BroadbandInfo.class);

      BroadbandInfo broadbandInfo = adapter.fromJson(jsonBroadband);
      /*
      List<List<String>> mapped = adapter.fromJson(jsonBroadband);
      HashMap<String, String> stateMap = new HashMap<>();
      for (int i = 0; i < mapped.size(); i++) {
        stateMap.put(mapped.get(i).get(0), mapped.get(i).get(1));
      }
      */
      return broadbandInfo;
    }
    // Returns an empty activity... Probably not the best handling of this error case...
    // Notice an alternative error throwing case to the one done in OrderHandler. This catches
    // the error instead of pushing it up.
    catch (IOException e) {
      e.printStackTrace();
      return new BroadbandInfo();
    }
  }

  public static HashMap<String, String> deserializeBroadbandIntoStateMap(String jsonBroadband) {
    try {
      // Initializes Moshi
      Moshi moshi = new Moshi.Builder().build();

      // Initializes an adapter to an Activity class then uses it to parse the JSON.
      JsonAdapter<List> adapter = moshi.adapter(List.class);

      List<List<String>> mapped = adapter.fromJson(jsonBroadband);
      HashMap<String, String> stateMap = new HashMap<>();
      for (int i = 0; i < mapped.size(); i++) {
        stateMap.put(mapped.get(i).get(0), mapped.get(i).get(1));
      }

      return stateMap;
    }
    // Returns an empty activity... Probably not the best handling of this error case...
    // Notice an alternative error throwing case to the one done in OrderHandler. This catches
    // the error instead of pushing it up.
    catch (IOException e) {
      e.printStackTrace();
      return new HashMap<String, String>();
    }
  }


  public static HashMap<String, String> deserializeBroadbandCounty(String jsonBroadband) {
    try {
      // Initializes Moshi
      Moshi moshi = new Moshi.Builder().build();

      // Initializes an adapter to an Activity class then uses it to parse the JSON.
      JsonAdapter<List> adapter = moshi.adapter(List.class);

      List<List<String>> mapped = adapter.fromJson(jsonBroadband);
      HashMap<String, String> countyMap = new HashMap<>();
      for (int i = 0; i < mapped.size(); i++) {
        String county = mapped.get(i).get(0);
        if (county.contains(",")) {
          String[] countyArr = county.split(",");
          county = countyArr[0];
        }
        countyMap.put(county, mapped.get(i).get(2));
      }
      return countyMap;
    }
    // Returns an empty activity... Probably not the best handling of this error case...
    // Notice an alternative error throwing case to the one done in OrderHandler. This catches
    // the error instead of pushing it up.
    catch (IOException e) {
      e.printStackTrace();
      return new HashMap<String, String>();
    }
  }


  public static List<List<String>> deserializeBroadbandData(String jsonBroadband) {
    try {
      // Initializes Moshi
      Moshi moshi = new Moshi.Builder().build();

      // Initializes an adapter to an Activity class then uses it to parse the JSON.
      JsonAdapter<List> adapter = moshi.adapter(List.class);

      List<List<String>> mapped = adapter.fromJson(jsonBroadband);
      return mapped;
    }
    // Returns an empty activity... Probably not the best handling of this error case...
    // Notice an alternative error throwing case to the one done in OrderHandler. This catches
    // the error instead of pushing it up.
    catch (IOException e) {
      e.printStackTrace();
        return new ArrayList<>();
    }
  }

  public static BroadbandInfo makeBroadbandInfo(String county, String state) {
    return new BroadbandInfo(county, state);
  }

}

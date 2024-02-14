package Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static HashMap<String, Integer> deserializeBroadband(String jsonBroadband) {
        try {
            // Initializes Moshi
            Moshi moshi = new Moshi.Builder().build();
            Type hashmapType = Types.newParameterizedType(Map.class, String.class, Integer.class);

            // Initializes an adapter to an Activity class then uses it to parse the JSON.
            JsonAdapter<HashMap<String,Integer>> adapter = moshi.adapter(hashmapType);

            HashMap<String, Integer> mapped = adapter.fromJson(jsonBroadband);

            return mapped;
        }
        // Returns an empty activity... Probably not the best handling of this error case...
        // Notice an alternative error throwing case to the one done in OrderHandler. This catches
        // the error instead of pushing it up.
        catch (IOException e) {
            e.printStackTrace();
            return new HashMap<String, Integer>();
        }
    }
}

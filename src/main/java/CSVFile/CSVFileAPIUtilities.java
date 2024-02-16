package CSVFile;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import java.io.IOException;

/**
 * This class shows a possible implementation of deserializing JSON from the BoredAPI into a
 * CSVFile.
 */
public class CSVFileAPIUtilities {

  /**
   * Deserializes JSON from the BoredAPI into an CSVFile object.
   *
   * @param jsonCSVFile
   * @return
   */
  public static CSVFile deserializeCSVFile(String jsonCSVFile) {
    try {
      // Initializes Moshi
      Moshi moshi = new Moshi.Builder().build();

      // Initializes an adapter to an Activity class then uses it to parse the JSON.
      JsonAdapter<CSVFile> adapter = moshi.adapter(CSVFile.class);

      CSVFile csvFile = adapter.fromJson(jsonCSVFile);

      return csvFile;
    }
    // Returns an empty activity... Probably not the best handling of this error case...
    // Notice an alternative error throwing case to the one done in OrderHandler. This catches
    // the error instead of pushing it up.
    catch (IOException e) {
      e.printStackTrace();
      return new CSVFile();
    }
  }
}

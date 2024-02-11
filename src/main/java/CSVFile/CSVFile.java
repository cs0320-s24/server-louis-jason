package CSVFile;

/**
 * This is a class that models an Activity received from the BoredAPI. It doesn't have a lot but
 * there are a few fields that you could filter on if you wanted!
 */
public class CSVFile {
  private String path;
  private String searchTerm;
  private boolean headers;
  private String identifier;
  private String headerNameOrIndex;

  public CSVFile() {}

  @Override
  public String toString() {
    return this.path;
  }
}

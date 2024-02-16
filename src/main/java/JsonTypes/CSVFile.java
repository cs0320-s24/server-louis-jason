package JsonTypes;

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

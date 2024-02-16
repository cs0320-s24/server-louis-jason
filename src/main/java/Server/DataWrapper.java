package Server;

import Creator.FactoryFailureException;
import Parser.CSVParse;
import java.io.IOException;
import java.util.List;

/**
 * This is a DataWrapper class. This wraps an instance of the CSV Parser. This is used in the
 * CSVFileHandlers. This keeps track of the most current parsed file that was requested by the user
 * and is used in the handlers.
 *
 * @param <T>
 */
public class DataWrapper<T> {
  private CSVParse<T> parser;
  private List<T> parsedData = null;

  /** Constructor wraps the instance of Parse */
  public DataWrapper(CSVParse<T> parser) {
    this.parser = parser;
  }

  public CSVParse<T> getCSVParser() {
    return this.parser;
  }

  /**
   * Method is used to update the parser if a new CSV file was loaded in.
   *
   * @param parser
   */
  public void setCSVParser(CSVParse<T> parser) {
    this.parser = parser;
  }

  /**
   * This method is used to parse the CSV file and returns the parsed data.
   *
   * @return
   * @throws IOException
   * @throws FactoryFailureException
   */
  public List<T> parseCSV() throws IOException, FactoryFailureException {
    if (this.parsedData == null) {
      this.parsedData = this.parser.parse();
    }
    return this.parsedData;
  }
}

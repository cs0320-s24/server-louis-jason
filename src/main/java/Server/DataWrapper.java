package Server;

import Creator.FactoryFailureException;
import Parser.CSVParse;
import java.io.IOException;
import java.util.List;

public class DataWrapper<T> {
  private CSVParse<T> parser;
  private List<T> parsedData = null;

  public DataWrapper(CSVParse<T> parser) {
    this.parser = parser;
  }

  public CSVParse<T> getCSVParser() {
    return this.parser;
  }

  public void setCSVParser(CSVParse<T> parser) {
    this.parser = parser;
  }

  public List<T> parseCSV() throws IOException, FactoryFailureException {
    if (this.parsedData == null) {
      this.parsedData = this.parser.parse();
    }
    return this.parsedData;
  }
}

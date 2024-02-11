package Parser;

import Creator.CreatorFromRow;
import Creator.FactoryFailureException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.regex.Pattern;

/**
 * This is the CSVParse class. This class takes in a reader object (where the CSV file is) and takes
 * in a class that implements the CreatorFromRow interface. This allows the class to be used in many
 * different ways and allows it to return a list of many different objects.
 *
 * @param <T>
 */
public class CSVParse<T> implements Iterable<T> {
  static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");
  private Reader file;
  private CreatorFromRow<T> creator;
  private List<T> objectList;

  public CSVParse(Reader file, CreatorFromRow<T> T) {
    this.file = file;
    this.creator = T;
  }

  /**
   * This is the parse function. Using generics, this parse function is applicable for many
   * functions for turning a row in a CSV file into list of object T.
   *
   * @return a List of object T
   * @throws IOException
   * @throws FactoryFailureException
   */
  public List<T> parse() throws IOException, FactoryFailureException {
    // Buffer the reader object
    BufferedReader reader = new BufferedReader(this.file);
    // read in a row of data
    String row = reader.readLine();
    // create a list of object T
    this.objectList = new ArrayList<>();
    while (row != null) {
      // parse the row to get rid of commas and etc.
      String[] result = regexSplitCSVRow.split(row);
      // turn the array into a list
      List<String> parsedRow = Arrays.asList(result);
      // convert it into object T
      T fullyParsed = this.creator.create(parsedRow);
      // add the object T to a list
      this.objectList.add(fullyParsed);
      // read the next line
      row = reader.readLine();
    }
    return this.objectList;
  }

  @Override
  public Iterator<T> iterator() {
    return this.objectList.iterator();
  }
}

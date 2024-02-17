package Searcher;

import static java.lang.Integer.parseInt;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the Search class. This takes in all the information provided by the user in the Main
 * class. From this information we search the parsed out data for the value that user wants.
 */
public class Search {
  private String value;
  private boolean booleanHeader;
  private String identifier;
  private boolean typeOfIdentifier;
  private List<List<String>> objectList;
  private int numberOfColumns;
  private List<List<String>> searchList;

  public Search(
      String value,
      boolean booleanHeader,
      String identifier,
      boolean typeOfIdentifier,
      List<List<String>> objectList) {
    this.value = value;
    this.booleanHeader = booleanHeader;
    this.identifier = identifier;
    this.typeOfIdentifier = typeOfIdentifier;
    this.objectList = objectList;
  }

  /**
   * This is the search method. It uses the information given by the user to find the value (if it's
   * in the data) If it finds a row that contains the value, then it will print the whole row to the
   * terminal. If there is malformed data, then the search function will skip that row and try to
   * continue searching for the value.
   */
  public List<List<String>> searches() throws Exception {
    this.searchList = new ArrayList<>();
    // this finds the number of columns that is in the very first row of the CSV file
    int numberOfColumns = this.objectList.get(0).size();
    this.numberOfColumns = numberOfColumns;
    int identifier;
    if (this.typeOfIdentifier) {
      // if the identifier is an integer, then we will parse what they typed into an integer.
      try {
        identifier = parseInt(this.identifier);
      } catch (NumberFormatException e) {
        // if cant turn the string into a number, throw exception
        throw new Exception("Column identifier needs to be an integer");
      }
      // error checking that the identifier is a reasonable number to use
      if (identifier + 1 > numberOfColumns) {
        throw new Exception("Column identifier out of range (max columns: " + numberOfColumns + ")");
      }
      int row = 0;
      // go through object list to find if the value is within it
      for (List<String> strings : this.objectList) {
        // error checks for malformed data
        if (strings.size() != numberOfColumns) {
          throw new Exception(
              "Skipped row "
                  + row
                  + "Number of columns in this row\n"
                  + " does not equal number of columns of the very first row in data file.");
        }
        // this is where we check if the value is within a row
        else if (this.value.equals(strings.get(identifier))) {
          this.searchList.add(strings);
        }
        row++;
      }
    } else {
      // if given the name of header
      if (this.booleanHeader && !this.identifier.equals("N/A")) {
        for (int i = 0; i < numberOfColumns; i++) {
          // search for what column the header is in
          if (this.identifier.equals(this.objectList.get(0).get(i))) {
            // once we find the header we assign it its number
            identifier = i;
            int row2 = 0;
            // go through object list to find the value in the column
            for (List<String> strings : this.objectList) {
              // error checking for malformed data
              if (strings.size() != numberOfColumns) {
                throw new Exception(
                    "Skipped row "
                        + row2
                        + " due to malformed data. Number of columns in this row\n"
                        + " does not equal number of columns of the very first row in data file.");
              }
              // sees if the column value is equal to the value specified by the user
              else if (this.value.equals(strings.get(identifier))) {
                this.searchList.add(strings);
              }
              row2++;
            }
            // if we get to this point then we have searched the CSV and want to leave the function
            return this.searchList;
          }
        }
        // if we are here then the header is not in the file so call helper function.
        throw new Exception("Header not found. Available headers: " + this.objectList.get(0));
      } else {
        // if there are no headers or entered a N/A then we are here
        // we must search through all of the csv file to look for the value we want
        helperNoHeaders();
      }
    }
    return this.searchList;
  }

  /**
   * get function so that it can be used in the testsuite
   *
   * @return
   */
  public List<List<String>> getSearchList() {
    return this.searchList;
  }

  /**
   * helper function that goes through all of csv file when there are no headers or headers cant be
   * used
   */
  private void helperNoHeaders() throws Exception {
    int row3 = 0;
    // we must search through all of the csv file to look for the value we want
    for (List<String> strings : this.objectList) {
      // check for malformed data
      if (strings.size() != this.numberOfColumns) {
        throw new Exception(
            "Skipped row "
                + row3
                + " due to malformed data. Number of columns in this row\n"
                + " does not equal number of columns of the very first row in data file.");
      } else {
        // if not a malformed row then check each column of the row for the value
        for (int i = 0; i < this.numberOfColumns; i++) {
          if (this.value.equals(strings.get(i))) {
            this.searchList.add(strings);
          }
        }
      }
      row3++;
    }
  }
}

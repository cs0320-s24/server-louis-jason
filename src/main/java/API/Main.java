package API;

import Creator.Creator;
import Creator.FactoryFailureException;
import Parser.CSVParse;
import Searcher.Search;
import java.io.*;
import java.util.List;

/** The Main class of our project. This is where execution begins. */
public final class Main {
  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private Main(String[] args) {}

  /**
   * This is the run method. This method is a REPL that takes in users responses to gather
   * information about a CSV file that they provide. In the information the REPL receives, it parses
   * the CSV file and searches the CSV file for the value that the user specifies.
   */
  private void run() {
    // this sets up the reader that reads in the input of the user
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    System.out.println(
        "All answers are case sensitive. Please enter the name of the filename.\n"
            + "It must be in the data folder, do not include \"data/\" in the filename.");
    // instantiating variable for filename and taking in next input by user as the filename.
    String fileName;
    try {
      fileName = reader.readLine();
    } catch (IOException e) {
      // program terminates if receives IOException
      System.err.println("error: " + e);
      return;
    }
    // if the user typed in a file that contains ".." then it reprompts them to enter a new filename
    // in order to keep in mind defensive programming and keep files private
    while (fileName.contains("..")) {
      System.out.println(
          "Entered invalid directory of \"..\". Please enter the name of the filename.\n"
              + "It must be in the data folder, do not include \"data/\" in the filename.");
      try {
        fileName = reader.readLine();
      } catch (IOException e) {
        // if get IOException terminates the program
        System.err.println("error: " + e);
        return;
      }
    }
    // append data to beginning of filename just as a precaution and for defensive programming.
    fileName = "data/" + fileName;
    System.out.println("Please enter the value you are looking for.");
    // instantiate the value that the user wants to look for.
    String value;
    try {
      value = reader.readLine();
    } catch (IOException e) {
      // if get IOException terminates the program
      System.err.println("error: " + e);
      return;
    }

    System.out.println("Are there headers in the CSV file? Please answer yes or no.");
    // instantiate the checkHeaders boolean that tells us if the file contains headers.
    String checkHeaders;
    try {
      checkHeaders = reader.readLine();
    } catch (IOException e) {
      // if get IOException terminates the program
      System.err.println("error: " + e);
      return;
    }
    // if the user does not enter yes or no to the previous question it will reprompt them until
    // they do
    while (!(checkHeaders.equals("yes") || checkHeaders.equals("no"))) {
      System.out.println("Are there headers in the CSV file? Please answer yes or no.");
      try {
        checkHeaders = reader.readLine();
      } catch (IOException e) {
        // if get IOException terminates the program
        System.err.println("error: " + e);
        return;
      }
    }
    // know it must be either yes or no at this point so this will give true or false to our boolean
    boolean booleanHeader = checkHeaders.equals("yes");
    // instantiating and asking for a column identifier.
    // This could be the name of a header or a number for a specific column.
    System.out.println(
        "Please enter a column identifier. If entering an integer, keep in mind 0 is the column "
            + "furthest to the left.\nIf entering column name please type it out as it appears on file.\n"
            + "If you do not wish to enter one please please type \"N/A\" without the quotation marks.");
    String identifier;
    try {
      identifier = reader.readLine();
    } catch (IOException e) {
      // if get IOException terminates the program
      System.err.println("error: " + e);
      return;
    }
    // clarification question on the type of the previous identifier which simplifies code in the
    // Search class
    System.out.println(
        "Was the column identifier you just typed an integer? Please answer yes or no.");
    // instantiate the typeOfIdentifer which will turn into a boolean determining if it is an
    // interger or not
    String typeOfIdentifier;
    try {
      typeOfIdentifier = reader.readLine();
    } catch (IOException e) {
      // if get IOException terminates the program
      System.err.println("error: " + e);
      return;
    }
    // if the user does not enter yes or no to the previous question it will reprompt them until
    // they do
    while (!(typeOfIdentifier.equals("yes") || typeOfIdentifier.equals("no"))) {
      System.out.println(
          "Was the column identifier you just typed an integer? Please answer yes or no.");
      try {
        typeOfIdentifier = reader.readLine();
      } catch (IOException e) {
        System.err.println("error: " + e);
        return;
      }
    }
    // know it must be either yes or no at this point so this will give true or false
    boolean indentifierIntegerBoolean = typeOfIdentifier.equals("yes");
    // instance of creator class that returns a List<Strings>
    Creator creator = new Creator();
    // create a filereader for the filename they provided
    FileReader fileReader;
    try {
      fileReader = new FileReader(fileName);
    } catch (FileNotFoundException e) {
      // if can't find the file then catches the exception and terminates the program
      System.err.println("error: " + e);
      return;
    }
    // create an instance of CSVParse to parse the file
    CSVParse<List<String>> parser = new CSVParse<>(fileReader, creator);
    List<List<String>> searchableList;
    try {
      // tries to parse
      searchableList = parser.parse();
    } catch (IOException | FactoryFailureException e) {
      // catches exception and then proceeds to terminate the program if it gets one
      System.err.println("error: " + e);
      return;
    }
    // create an instance of my search class to look for something in the parsed out csv file that
    // was
    // specified by the user.
    Search searcher =
        new Search(value, booleanHeader, identifier, indentifierIntegerBoolean, searchableList);
    searcher.searches();
  }
}

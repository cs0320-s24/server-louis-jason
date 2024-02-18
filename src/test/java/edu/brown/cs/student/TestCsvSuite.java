package edu.brown.cs.student;

import static org.junit.jupiter.api.Assertions.assertThrows;

import Creator.Creator;
import Creator.CreatorTestEarnings;
import Creator.FactoryFailureException;
import Creator.Person;
import Parser.CSVParse;
import Searcher.Search;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

public class TestCsvSuite {
  @Test
  public void testBasicParse() {
    Creator creator = new Creator();
    FileReader fileReader;
    String fileName = "data/census/dol_ri_earnings_disparity_small.csv";
    try {
      fileReader = new FileReader(fileName);
    } catch (IOException e) {
      Assert.fail("IOException");
      return;
    }
    CSVParse<List<String>> parser = new CSVParse<>(fileReader, creator);
    List<List<String>> searchableList;
    try {
      searchableList = parser.parse();
    } catch (IOException | FactoryFailureException e) {
      Assert.fail("IOException or FactoryFailureException");
      return;
    }
    List<String> internal = new ArrayList<>();
    internal.add("State");
    internal.add("Data Type");
    internal.add("Average Weekly Earnings");
    List<String> internal1 = new ArrayList<>();
    internal1.add("RI");
    internal1.add("White");
    internal1.add("\" $1,058.47 \"");
    List<String> internal2 = new ArrayList<>();
    internal2.add("RI");
    internal2.add("Black");
    internal2.add("$770.26");
    List<List<String>> testList = new ArrayList<>();
    testList.add(internal);
    testList.add(internal1);
    testList.add(internal2);
    Assert.assertEquals(searchableList, testList);
  }

  @Test
  public void testBasicSearch() throws Exception {
    Creator creator = new Creator();
    FileReader fileReader;
    String fileName = "data/census/dol_ri_earnings_disparity_small.csv";
    try {
      fileReader = new FileReader(fileName);
    } catch (IOException e) {
      Assert.fail("IOException");
      return;
    }
    CSVParse<List<String>> parser = new CSVParse<>(fileReader, creator);
    List<List<String>> searchableList;
    try {
      searchableList = parser.parse();
    } catch (IOException | FactoryFailureException e) {
      Assert.fail("IOException or FactoryFailureException");
      return;
    }
    // testing with headers
    String value = "RI";
    boolean booleanHeader = true;
    // testing with integer identifier with headers
    String identifier = "0";
    boolean indentifierIntegerBoolean = true;
    Search searcher =
        new Search(value, booleanHeader, identifier, indentifierIntegerBoolean, searchableList);

    // testing with string identifier with headers
    String identifier1 = "State";
    boolean indentifierIntegerBoolean1 = false;
    Search searcher1 =
        new Search(value, booleanHeader, identifier1, indentifierIntegerBoolean1, searchableList);

    // testing with no headers and no identifiers
    boolean booleanHeader1 = false;
    String identifier2 = "N/A";
    Search searcher2 =
        new Search(value, booleanHeader1, identifier2, indentifierIntegerBoolean1, searchableList);

    // testing with no headers and string identifier
    Search searcher3 =
        new Search(value, booleanHeader1, identifier1, indentifierIntegerBoolean1, searchableList);

    // testing with no headers and integer identifier
    Search searcher4 =
        new Search(value, booleanHeader1, identifier, indentifierIntegerBoolean1, searchableList);

    List<String> internal1 = new ArrayList<>();
    internal1.add("RI");
    internal1.add("White");
    internal1.add("\" $1,058.47 \"");
    List<String> internal2 = new ArrayList<>();
    internal2.add("RI");
    internal2.add("Black");
    internal2.add("$770.26");
    List<List<String>> testList = new ArrayList<>();
    testList.add(internal1);
    testList.add(internal2);
    Assert.assertEquals(searcher.searches(), testList);
    Assert.assertEquals(searcher1.searches(), testList);
    Assert.assertEquals(searcher2.searches(), testList);
    Assert.assertEquals(searcher3.searches(), testList);
    Assert.assertEquals(searcher4.searches(), testList);
  }

  @Test
  public void testMalformedSearch() throws Exception {
    Creator creator = new Creator();
    FileReader fileReader;
    String fileName = "data/malformed/malformed_signs.csv";
    try {
      fileReader = new FileReader(fileName);
    } catch (IOException e) {
      Assert.fail("IOException");
      return;
    }
    CSVParse<List<String>> parser = new CSVParse<>(fileReader, creator);
    List<List<String>> searchableList;
    try {
      searchableList = parser.parse();
    } catch (IOException | FactoryFailureException e) {
      Assert.fail("IOException or FactoryFailureException");
      return;
    }
    // testing with headers
    String value = "Capricorn";
    boolean booleanHeader = true;
    // testing with integer identifier with headers
    String identifier = "0";
    boolean indentifierIntegerBoolean = true;
    Search searcher =
        new Search(value, booleanHeader, identifier, indentifierIntegerBoolean, searchableList);

    // testing with string identifier with headers
    String identifier1 = "Star Sign";
    boolean indentifierIntegerBoolean1 = false;
    Search searcher1 =
        new Search(value, booleanHeader, identifier1, indentifierIntegerBoolean1, searchableList);

    // testing with no headers and no identifiers
    boolean booleanHeader1 = false;
    String identifier2 = "N/A";
    Search searcher2 =
        new Search(value, booleanHeader1, identifier2, indentifierIntegerBoolean1, searchableList);

    // testing with no headers and string identifier
    Search searcher3 =
        new Search(value, booleanHeader1, identifier1, indentifierIntegerBoolean1, searchableList);

    // testing with no headers and integer identifier
    Search searcher4 =
        new Search(value, booleanHeader1, identifier, indentifierIntegerBoolean1, searchableList);

    List<String> internal1 = new ArrayList<>();
    internal1.add("Capricorn");
    internal1.add("Sophie");
    List<List<String>> testList = new ArrayList<>();
    testList.add(internal1);
    Exception exception =
        assertThrows(
            Exception.class,
            () -> {
              searcher.searches();
            });
    Exception exception1 =
        assertThrows(
            Exception.class,
            () -> {
              searcher1.searches();
            });
    Exception exception2 =
        assertThrows(
            Exception.class,
            () -> {
              searcher2.searches();
            });
    Exception exception3 =
        assertThrows(
            Exception.class,
            () -> {
              searcher3.searches();
            });
    Exception exception4 =
        assertThrows(
            Exception.class,
            () -> {
              searcher4.searches();
            });
  }

  @Test
  public void testDifferentReaderSearch() throws Exception {
    Creator creator = new Creator();
    StringReader fileReader;
    String fileName =
        "Star Sign,Member,\n"
            + "Aries,Annie,\n"
            + "Taurus,Albert\n"
            + "Gemini,Roberto,Nick\n"
            + "Cancer,Alexis,\n"
            + "Leo,Gabi,\n"
            + "Virgo,\n"
            + "Libra,,\n"
            + "Scorpio,Nicole,\n"
            + "Sagittarius,Tim,\n"
            + "Capricorn,Sophie,\n"
            + "Aquarius,,\n"
            + "Pisces,Danny,";
    fileReader = new StringReader(fileName);
    CSVParse<List<String>> parser = new CSVParse<>(fileReader, creator);
    List<List<String>> searchableList;
    try {
      searchableList = parser.parse();
    } catch (IOException | FactoryFailureException e) {
      Assert.fail("IOException or FactoryFailureException");
      return;
    }
    // testing with headers
    String value = "Capricorn";
    boolean booleanHeader = true;
    // testing with integer identifier with headers
    String identifier = "0";
    boolean indentifierIntegerBoolean = true;
    Search searcher =
        new Search(value, booleanHeader, identifier, indentifierIntegerBoolean, searchableList);

    // testing with string identifier with headers
    String identifier1 = "Star Sign";
    boolean indentifierIntegerBoolean1 = false;
    Search searcher1 =
        new Search(value, booleanHeader, identifier1, indentifierIntegerBoolean1, searchableList);

    // testing with no headers and no identifiers
    boolean booleanHeader1 = false;
    String identifier2 = "N/A";
    Search searcher2 =
        new Search(value, booleanHeader1, identifier2, indentifierIntegerBoolean1, searchableList);

    // testing with no headers and string identifier
    Search searcher3 =
        new Search(value, booleanHeader1, identifier1, indentifierIntegerBoolean1, searchableList);

    // testing with no headers and integer identifier
    Search searcher4 =
        new Search(value, booleanHeader1, identifier, indentifierIntegerBoolean1, searchableList);

    List<String> internal1 = new ArrayList<>();
    internal1.add("Capricorn");
    internal1.add("Sophie");
    List<List<String>> testList = new ArrayList<>();
    testList.add(internal1);
    Exception exception =
        assertThrows(
            Exception.class,
            () -> {
              searcher.searches();
            });
    Exception exception1 =
        assertThrows(
            Exception.class,
            () -> {
              searcher1.searches();
            });
    Exception exception2 =
        assertThrows(
            Exception.class,
            () -> {
              searcher2.searches();
            });
    Exception exception3 =
        assertThrows(
            Exception.class,
            () -> {
              searcher3.searches();
            });
    Exception exception4 =
        assertThrows(
            Exception.class,
            () -> {
              searcher4.searches();
            });
  }

  @Test
  public void testBasicSearchValueNotPresent() throws Exception {
    Creator creator = new Creator();
    FileReader fileReader;
    String fileName = "data/census/dol_ri_earnings_disparity_small.csv";
    try {
      fileReader = new FileReader(fileName);
    } catch (IOException e) {
      Assert.fail("IOException");
      return;
    }
    CSVParse<List<String>> parser = new CSVParse<>(fileReader, creator);
    List<List<String>> searchableList;
    try {
      searchableList = parser.parse();
    } catch (IOException | FactoryFailureException e) {
      Assert.fail("IOException or FactoryFailureException");
      return;
    }
    // testing with headers
    String value = "blahblah";
    boolean booleanHeader = true;
    // testing with integer identifier with headers
    String identifier = "0";
    boolean indentifierIntegerBoolean = true;
    Search searcher =
        new Search(value, booleanHeader, identifier, indentifierIntegerBoolean, searchableList);

    // testing with string identifier with headers
    String identifier1 = "State";
    boolean indentifierIntegerBoolean1 = false;
    Search searcher1 =
        new Search(value, booleanHeader, identifier1, indentifierIntegerBoolean1, searchableList);

    // testing with no headers and no identifiers
    boolean booleanHeader1 = false;
    String identifier2 = "N/A";
    Search searcher2 =
        new Search(value, booleanHeader1, identifier2, indentifierIntegerBoolean1, searchableList);

    // testing with no headers and string identifier
    Search searcher3 =
        new Search(value, booleanHeader1, identifier1, indentifierIntegerBoolean1, searchableList);

    // testing with no headers and integer identifier
    Search searcher4 =
        new Search(value, booleanHeader1, identifier, indentifierIntegerBoolean1, searchableList);

    List<List<String>> testList = new ArrayList<>();

    Assert.assertEquals(searcher.searches(), testList);
    Assert.assertEquals(searcher1.searches(), testList);
    Assert.assertEquals(searcher2.searches(), testList);
    Assert.assertEquals(searcher3.searches(), testList);
    Assert.assertEquals(searcher4.searches(), testList);
  }

  @Test
  public void testBasicSearchValueInWrongColumn() throws Exception {
    Creator creator = new Creator();
    FileReader fileReader;
    String fileName = "data/census/dol_ri_earnings_disparity_small.csv";
    try {
      fileReader = new FileReader(fileName);
    } catch (IOException e) {
      Assert.fail("IOException");
      return;
    }
    CSVParse<List<String>> parser = new CSVParse<>(fileReader, creator);
    List<List<String>> searchableList;
    try {
      searchableList = parser.parse();
    } catch (IOException | FactoryFailureException e) {
      Assert.fail("IOException or FactoryFailureException");
      return;
    }
    // testing with headers
    String value = "RI";
    boolean booleanHeader = true;
    // testing with integer identifier with headers
    String identifier = "1";
    boolean indentifierIntegerBoolean = true;
    Search searcher =
        new Search(value, booleanHeader, identifier, indentifierIntegerBoolean, searchableList);

    // testing with string identifier with headers
    String identifier1 = "Data Type";
    boolean indentifierIntegerBoolean1 = false;
    Search searcher1 =
        new Search(value, booleanHeader, identifier1, indentifierIntegerBoolean1, searchableList);

    // testing with no headers and no identifiers
    boolean booleanHeader1 = false;
    String identifier2 = "N/A";
    Search searcher2 =
        new Search(value, booleanHeader1, identifier2, indentifierIntegerBoolean1, searchableList);

    // testing with no headers and string identifier
    Search searcher3 =
        new Search(value, booleanHeader1, identifier1, indentifierIntegerBoolean1, searchableList);

    // testing with no headers and integer identifier
    Search searcher4 =
        new Search(value, booleanHeader1, identifier, indentifierIntegerBoolean1, searchableList);

    List<String> internal1 = new ArrayList<>();
    internal1.add("RI");
    internal1.add("White");
    internal1.add("\" $1,058.47 \"");
    List<String> internal2 = new ArrayList<>();
    internal2.add("RI");
    internal2.add("Black");
    internal2.add("$770.26");
    List<List<String>> testList = new ArrayList<>();
    testList.add(internal1);
    testList.add(internal2);

    List<List<String>> testList1 = new ArrayList<>();

    // will return nothing
    Assert.assertEquals(searcher.searches(), testList1);
    Assert.assertEquals(searcher1.searches(), testList1);
    // will return like usual
    Assert.assertEquals(searcher2.searches(), testList);
    Assert.assertEquals(searcher3.searches(), testList);
    Assert.assertEquals(searcher4.searches(), testList);
  }

  @Test
  public void testBasicSearchDifferentCreator() {
    CreatorTestEarnings creator = new CreatorTestEarnings();
    FileReader fileReader;
    String fileName = "data/census/dol_ri_earnings_disparity_small.csv";
    try {
      fileReader = new FileReader(fileName);
    } catch (IOException e) {
      Assert.fail("IOException");
      return;
    }
    CSVParse<Person> parser = new CSVParse<>(fileReader, creator);
    List<Person> searchableList;
    try {
      searchableList = parser.parse();
    } catch (IOException | FactoryFailureException e) {
      Assert.fail("IOException or FactoryFailureException");
      return;
    }
    Person person1 = new Person("State", "Data Type", "Average Weekly Earnings");
    Person person2 = new Person("RI", "White", "\" $1,058.47 \"");
    Person person3 = new Person("RI", "Black", "$770.26");

    List<Person> testList = new ArrayList<>();
    testList.add(person1);
    testList.add(person2);
    testList.add(person3);
    boolean check = true;
    if (!testList.get(1).equal(searchableList.get(1))) {
      check = false;
    }
    if (!testList.get(2).equal(searchableList.get(2))) {
      check = false;
    }
    Assert.assertEquals(check, true);
  }
}

package edu.brown.cs.student.main.CSV;

import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Searcher {

  /**
   * The `Searcher` class is responsible for searching CSV data based on specified criteria such as
   * column name, column index, or with no headers. It can search for a specific item within the CSV
   * data and return the rows that match the search criteria. This class uses a `Parser` to parse
   * the CSV data and performs searches based on the provided parameters.
   */
  Parser p;

  ArrayList<ArrayList<String>> listOfObjects;
  String itemToSearchFor;
  int columnNumber;
  String columnName;
  private List<List<String>> result;

  /**
   * Constructs a `Searcher` object to perform a search based on column name.
   *
   * @param fileName The name of the CSV file to search within.
   * @param itemToSearchFor The item to search for within the CSV data.
   * @param columnName The name of the column to search within.
   * @throws FileNotFoundException If the CSV file is not found.
   */
  public Searcher(String fileName, String itemToSearchFor, String columnName)
      throws FileNotFoundException {

    this.itemToSearchFor = itemToSearchFor;

    Creator type = new Creator();
    BufferedReader reader = new BufferedReader(new FileReader(fileName));

    this.p = new Parser(reader, type);
    this.listOfObjects = p.parse();
    this.columnName = columnName;
    this.result = new ArrayList<>();
    searchWithColumnName();
  }

  /**
   * Searches for rows within the CSV data based on a specified column name. It converts the column
   * names to lowercase for case-insensitive matching, finds the index of the target column, and
   * then compares the values in that column to the provided item to search for matches. Matching
   * rows are added to the result.
   */
  public void searchWithColumnName() {

    List<String> headerRow = this.listOfObjects.get(0);

    List<String> headerRowLowerCase = new ArrayList<>();

    for (String header : headerRow) {
      headerRowLowerCase.add(header.toLowerCase());
    }

    int columnIndex = headerRowLowerCase.indexOf(this.columnName.toLowerCase());
    this.listOfObjects.remove(0);

    for (List<String> row : this.listOfObjects) {
      if (row.get(columnIndex).equals(this.itemToSearchFor)) {
        this.result.add(row);
      }
    }
  }

  /**
   * Performs a search based on column name and returns the matching rows.
   *
   * @return A list of lists containing the rows that match the search criteria.
   */
  public List<List<String>> getColumnNameResult() {
    return this.result;
  }

  /**
   * Constructs a `Searcher` object to perform a search based on column index.
   *
   * @param fileName The name of the CSV file to search within.
   * @param itemToSearchFor The item to search for within the CSV data.
   * @param columnNumber The index of the column to search within.
   * @throws IOException If there is an I/O error while reading the CSV file.
   * @throws FactoryFailureException If there is a factory failure.
   */
  public Searcher(String fileName, String itemToSearchFor, int columnNumber)
      throws IOException, FactoryFailureException {
    this.itemToSearchFor = itemToSearchFor;

    Creator type = new Creator();
    BufferedReader reader = new BufferedReader(new FileReader(fileName));

    this.p = new Parser(reader, type);
    this.listOfObjects = p.parse();
    this.columnNumber = columnNumber;
    this.result = new ArrayList<>();
    searchWithColumnNumber();
  }

  /**
   * Searches for rows within the CSV data based on a specified column number. It removes the header
   * row, then iterates through the remaining rows and compares the value in the specified column
   * with the provided item to search for matches. Matching rows are added to the result.
   */
  public void searchWithColumnNumber() {
    this.listOfObjects.remove(0);

    for (List<String> row : this.listOfObjects) {
      if (row.get(this.columnNumber).equals(this.itemToSearchFor)) {
        this.result.add(row);
      }
    }
  }

  /**
   * Performs a search based on column index and returns the matching rows.
   *
   * @return A list of lists containing the rows that match the search criteria.
   */
  public List<List<String>> getColumnNumberResult() {
    return this.result;
  }

  /**
   * Constructs a `Searcher` object to perform a search with no headers.
   *
   * @param fileName The name of the CSV file to search within.
   * @param itemToSearchFor The item to search for within the CSV data.
   * @throws IOException If there is an I/O error while reading the CSV file.
   * @throws FactoryFailureException If there is a factory failure.
   */
  public Searcher(String fileName, String itemToSearchFor)
      throws IOException, FactoryFailureException {
    this.itemToSearchFor = itemToSearchFor;

    Creator type = new Creator();
    BufferedReader reader = new BufferedReader(new FileReader(fileName));

    this.p = new Parser(reader, type);
    this.listOfObjects = p.parse();
    this.result = new ArrayList<>();
    searchWithNoHeaders();
  }

  /**
   * Searches for rows within the CSV data when there are no header rows. It iterates through all
   * rows and all items within each row, comparing each item with the provided item to search for
   * matches. Matching rows are added to the result.
   */
  public void searchWithNoHeaders() {

    for (List<String> row : this.listOfObjects) {
      for (String item : row) {
        if (item.equals(this.itemToSearchFor)) {
          this.result.add(row);
        }
      }
    }
  }

  /**
   * Performs a search with no headers and returns the matching rows.
   *
   * @return A list of lists containing the rows that match the search criteria.
   */
  public List<List<String>> getNoHeaderResult() {
    return this.result;
  }
}

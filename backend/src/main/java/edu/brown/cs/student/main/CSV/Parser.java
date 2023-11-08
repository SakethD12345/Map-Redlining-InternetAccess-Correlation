package edu.brown.cs.student.main.CSV;

import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * The `Parser` class is responsible for parsing CSV data from a provided `Reader` and converting it
 * into a list of objects of type `T`. It uses a `CreatorFromRow` implementation to create objects
 * from each CSV row, and it splits CSV rows into individual elements using a regular expression
 * pattern.
 *
 * @param <T> The type of objects to create from CSV rows.
 */
public class Parser<T> {

  private Reader r;
  private Pattern regexSplitCSVRow;
  private CreatorFromRow<T> type;
  private ArrayList<T> listOfObjects;

  /**
   * Constructs a new `Parser` instance with the specified `Reader` and a `CreatorFromRow`
   * implementation.
   *
   * @param r The `Reader` to read CSV data from.
   * @param type The `CreatorFromRow` implementation for creating objects from CSV rows.
   */
  public Parser(Reader r, CreatorFromRow<T> type) {
    this.r = r;
    this.type = type;
    this.listOfObjects = new ArrayList<>();

    // Regular expression pattern to split CSV rows
    this.regexSplitCSVRow = Pattern.compile(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\"))");
  }

  /**
   * Parses the CSV data from the provided `Reader` and converts it into a list of objects of type
   * `T`.
   *
   * @return A list of objects created from the parsed CSV data.
   * @throws RuntimeException If there are any issues during the parsing process, including I/O
   *     errors or failures in object creation.
   */
  public ArrayList<T> parse() {
    try {
      BufferedReader reader = new BufferedReader(this.r);
      String line = reader.readLine();

      while (line != null) {
        List<String> newLine = Arrays.stream(this.regexSplitCSVRow.split(line)).toList();
        List<String> cleanLine =
            newLine.stream().map(item -> item.replaceAll("[\"']", "")).collect(Collectors.toList());
        this.listOfObjects.add(this.type.create(cleanLine));
        line = reader.readLine();
      }

      reader.close();

      return this.listOfObjects;
    } catch (IOException | FactoryFailureException e) {
      throw new RuntimeException(e);
    }
  }
}

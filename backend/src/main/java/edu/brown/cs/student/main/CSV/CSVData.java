package edu.brown.cs.student.main.CSV;

import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * The `CSVData` class represents a utility for managing and parsing CSV data within a web
 * application. It allows setting and retrieving the file path to a CSV file and provides a method
 * for parsing the contents of the CSV file into a list of lists of strings.
 */
public class CSVData {

  private String filepath;

  /**
   * Constructs a new `CSVData` instance with an initial file path.
   *
   * @param filepath The initial file path to a CSV file.
   */
  public CSVData(String filepath) {
    this.filepath = filepath;
  }

  /**
   * Sets the file path to a CSV file.
   *
   * @param filepath The file path to set.
   */
  public void setFilePath(String filepath) {
    this.filepath = filepath;
  }

  /**
   * Retrieves the current file path to the CSV file.
   *
   * @return The current file path.
   */
  public String getFilePath() {
    return this.filepath;
  }

  /**
   * Parses the CSV file located at the specified file path and returns its contents as a list of
   * lists of strings.
   *
   * @param filepath The file path of the CSV file to parse.
   * @return A list of lists of strings representing the parsed CSV data.
   * @throws IOException If there are any issues during the parsing process, such as file I/O
   *     errors.
   */
  public List<List<String>> parseCSV(String filepath) throws IOException {
    try (FileReader fileReader = new FileReader(filepath)) {
      // Create a Parser instance with the appropriate CreatorFromRow implementation
      Parser<List<String>> parser = new Parser<>(fileReader, new CSVRowCreator());

      // Parse the CSV file using the Parser class
      return parser.parse();
    }
  }

  private static class CSVRowCreator implements CreatorFromRow<List<String>> {
    @Override
    public List<String> create(List<String> row) throws FactoryFailureException {
      return row;
    }
  }
}

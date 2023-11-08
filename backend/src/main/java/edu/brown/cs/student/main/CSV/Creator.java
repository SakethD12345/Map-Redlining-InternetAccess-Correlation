package edu.brown.cs.student.main.CSV;

import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import java.util.List;

/**
 * The `Creator` class implements the `CreatorFromRow` interface, which is responsible for creating
 * objects of a generic type `T` from a list of strings. In this implementation, the generic type
 * `T` is specifically set to `List<String>`, indicating that it creates lists of strings from rows
 * of data. This class is designed to be a simple identity creator, where it returns the input row
 * as-is without any transformation. It can be used when data rows need to be directly converted
 * into lists of strings.
 *
 * @param <T> The type of objects to be created from rows (typically `List<String>`).
 */
public class Creator<T> implements CreatorFromRow<List<String>> {

  /** Constructs a new instance of the `Creator` class. */
  public Creator() {}

  /**
   * Creates an object of type `List<String>` from a given row of strings.
   *
   * @param row The row of strings to create an object from.
   * @return An object of type `List<String>` representing the input row.
   * @throws FactoryFailureException If there are any issues during the creation process.
   */
  @Override
  public List<String> create(List<String> row) throws FactoryFailureException {
    return row;
  }
}

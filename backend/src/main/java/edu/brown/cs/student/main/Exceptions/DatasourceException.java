package edu.brown.cs.student.main.Exceptions;

/**
 * The `DatasourceException` class is a custom exception that extends the standard Java `Exception`
 * class. It is designed to represent exceptions specific to data source operations within a
 * software application. This exception may be used to indicate errors or unexpected conditions that
 * occur while accessing or interacting with data sources, providing a more specialized and
 * informative way to handle data-related issues.
 */
public class DatasourceException extends Exception {

  private final Throwable cause;

  /**
   * Constructs a new `DatasourceException` with the specified error message.
   *
   * @param message The error message that describes the exception.
   */
  public DatasourceException(String message) {
    super(message);
    this.cause = null;
  }

  /**
   * Constructs a new `DatasourceException` with the specified error message and a reference to the
   * underlying cause of the exception.
   *
   * @param message The error message that describes the exception.
   * @param cause The underlying cause of the exception.
   */
  public DatasourceException(String message, Throwable cause) {
    super(message);
    this.cause = cause;
  }
}

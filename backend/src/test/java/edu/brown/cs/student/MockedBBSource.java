package edu.brown.cs.student;

import edu.brown.cs.student.main.Broadband.BroadbandData;
import edu.brown.cs.student.main.Broadband.BroadbandDataSource;
import edu.brown.cs.student.main.Broadband.StateAndCounty;
import edu.brown.cs.student.main.Exceptions.DatasourceException;

/**
 * The `MockedBBSource` class is an implementation of the `BroadbandDataSource` interface that
 * provides mocked or constant data for broadband-related information. It is used primarily for
 * testing purposes to simulate data retrieval from an external source.
 */
public class MockedBBSource implements BroadbandDataSource {
  private final BroadbandData constantData;

  /**
   * Constructs a `MockedBBSource` object with the specified constant broadband data.
   *
   * @param constantData The constant broadband data to be returned by the data source.
   */
  public MockedBBSource(BroadbandData constantData) {
    this.constantData = constantData;
  }

  /**
   * Retrieves constant broadband data for the specified location. This method is used for testing
   * and always returns the same constant data provided during initialization.
   *
   * @param location The geographical location for which broadband data is requested.
   * @return The constant broadband data.
   * @throws DatasourceException If there is an issue accessing the data source.
   */
  @Override
  public BroadbandData getCurrentBroadband(StateAndCounty location) throws DatasourceException {
    return constantData;
  }

  /**
   * Retrieves the state code for the given state. This method is not implemented and returns null.
   *
   * @param state The name of the state.
   * @return The state code, or null if not implemented.
   * @throws DatasourceException If there is an issue accessing the data source.
   */
  @Override
  public String getStateCode(String state) throws DatasourceException {
    return null;
  }

  /**
   * Retrieves the county code for the specified state code and county name. This method is not
   * implemented and returns null.
   *
   * @param stateCode The state code.
   * @param county The name of the county.
   * @return The county code, or null if not implemented.
   * @throws DatasourceException If there is an issue accessing the data source.
   */
  @Override
  public String getCountyCode(String stateCode, String county) throws DatasourceException {
    return null;
  }

  /**
   * Retrieves the household percentage for the specified state code and county code. This method is
   * not implemented and returns null.
   *
   * @param stateCode The state code.
   * @param countyCode The county code.
   * @return The household percentage, or null if not implemented.
   * @throws DatasourceException If there is an issue accessing the data source.
   */
  @Override
  public String getHouseholdPercentage(String stateCode, String countyCode)
      throws DatasourceException {
    return null;
  }
}

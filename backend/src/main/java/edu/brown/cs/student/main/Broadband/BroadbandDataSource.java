package edu.brown.cs.student.main.Broadband;

import edu.brown.cs.student.main.Exceptions.DatasourceException;

/**
 * The `BroadbandDataSource` interface defines the contract for data sources that provide
 * information related to broadband availability. Implementing classes should offer methods to
 * retrieve broadband data for a given location, obtain state and county codes, and fetch household
 * percentage information.
 */
public interface BroadbandDataSource {

  /**
   * Retrieves the current broadband data for a specified location.
   *
   * @param location The state and county information for the location.
   * @return The broadband data for the specified location.
   * @throws DatasourceException If there are any issues accessing or retrieving the data.
   */
  BroadbandData getCurrentBroadband(StateAndCounty location) throws DatasourceException;

  /**
   * Retrieves the state code for a given state name.
   *
   * @param state The name of the state.
   * @return The state code corresponding to the provided state name.
   * @throws DatasourceException If there are any issues accessing or retrieving the data.
   */
  String getStateCode(String state) throws DatasourceException;

  /**
   * Retrieves the county code for a given state code and county name.
   *
   * @param stateCode The code of the state.
   * @param county The name of the county.
   * @return The county code corresponding to the provided state code and county name.
   * @throws DatasourceException If there are any issues accessing or retrieving the data.
   */
  String getCountyCode(String stateCode, String county) throws DatasourceException;

  /**
   * Retrieves household percentage information for a given state code and county code.
   *
   * @param stateCode The code of the state.
   * @param countyCode The code of the county.
   * @return The household percentage information for the specified state and county.
   * @throws DatasourceException If there are any issues accessing or retrieving the data.
   */
  String getHouseholdPercentage(String stateCode, String countyCode) throws DatasourceException;
}

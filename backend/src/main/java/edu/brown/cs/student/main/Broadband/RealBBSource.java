package edu.brown.cs.student.main.Broadband;

import edu.brown.cs.student.main.Exceptions.DatasourceException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * The `RealBBSource` class implements the `BroadbandDataSource` interface and is responsible for
 * retrieving real-world broadband data from external sources using API requests. It provides
 * methods to fetch the household percentage of broadband internet access for a specific state and
 * county, as well as to obtain state and county codes. The class uses HTTP requests to interact
 * with the Census API and handles various exceptions related to network communication and data
 * retrieval.
 */
public class RealBBSource implements BroadbandDataSource {

  /** Constructs a new `RealBBSource` instance. */
  public RealBBSource() {}

  @Override
  public BroadbandData getCurrentBroadband(StateAndCounty location) throws DatasourceException {
    return null;
  }

  /**
   * Retrieves the household broadband percentage for a given state and county using the Census API.
   *
   * @param stateCode The state code for the desired state.
   * @param countyCode The county code for the desired county.
   * @return The household broadband percentage as a string.
   */
  public String getHouseholdPercentage(String stateCode, String countyCode) {

    try {
      URL url =
          new URL(
              "https://api.census.gov/data/2021/acs/acs1/subject/variables?get"
                  + "=NAME,S2802_C03_022E&for=county:"
                  + countyCode
                  + "&in=state:"
                  + stateCode);
      // Open a connection to the URL
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      // Read the response from the API
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String line;
      StringBuilder response = new StringBuilder();

      while ((line = reader.readLine()) != null) {
        response.append(line);
      }
      reader.close();
      connection.disconnect();
      String apiResponse = response.toString();
      String[] lines = apiResponse.split("],");
      String[] result = new String[lines.length];
      for (int i = 0; i < lines.length; i++) {
        result[i] = lines[i].trim();
      }
      String row = result[1].substring(1);
      String[] results = row.split(",");
      System.out.println(results[2]);
      System.out.println("household percentage: " + row.substring(1).replace("\"", ""));
      return results[2].replace("\"", "");
    } catch (ProtocolException e) {
      throw new RuntimeException(e);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    // response includes date and time that all data was retrieved, and state and county names
    // received
    // state and county query parameters
  }

  /**
   * Retrieves the state code for a given state name using the Census API.
   *
   * @param state The name of the state.
   * @return The state code as a string.
   */
  public String getStateCode(String state) {

    try {

      URL url = new URL("https://api.census.gov/data/2010/dec/sf1?get=NAME&for=state:*");
      // Open a connection to the URL
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      // Read the response from the API
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String line;
      StringBuilder response = new StringBuilder();

      while ((line = reader.readLine()) != null) {
        response.append(line);
      }
      reader.close();
      connection.disconnect();
      String apiResponse = response.toString();
      String[] lines = apiResponse.split("],");
      String[] result = new String[lines.length];
      // Iterate through lines to store each line in the result array
      for (int i = 0; i < lines.length; i++) {
        result[i] = lines[i] + "]";
      }
      Map<String, String> stateCodeMap = new HashMap<>();
      for (String myResult : result) {
        String[] parts = myResult.replaceAll("\\[|\\]", "").split(",");
        if (parts.length == 2) {
          // Trim whitespace from state name and code
          String stateName = parts[0].trim().replaceAll("\"", "");
          String stateCode = parts[1].trim().replaceAll("\"", "");

          // Put the state name as the value and state code as the key
          stateCodeMap.put(stateName, stateCode);
        }
      }
      String code = stateCodeMap.get(state);

      if (code == null) {

        throw new Exception();
      }
      return code;
    } catch (Exception e) {

      throw new RuntimeException(e);
    }
    //    return null;
  }

  /**
   * Retrieves the county code for a given county name and state code using the Census API.
   *
   * @param stateCode The state code for the associated state.
   * @param county The name of the county.
   * @return The county code as a string.
   */
  public String getCountyCode(String stateCode, String county) {
    try {
      URL url =
          new URL(
              "https://api.census.gov/data/2010/dec/sf1?get=NAME&for=county:*&in=state:"
                  + stateCode);
      // Open a connection to the URL
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      // Read the response from the API
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String line;
      StringBuilder response = new StringBuilder();

      while ((line = reader.readLine()) != null) {
        response.append(line);
      }
      reader.close();
      connection.disconnect();
      String apiResponse = response.toString();
      String[] lines = apiResponse.split("\\],\\[");
      String[] result = new String[lines.length];
      // Iterate through lines to store each line in the result array
      for (int i = 0; i < lines.length; i++) {
        result[i] = lines[i];
      }
      Map<String, String> countyCodeMap = new HashMap<>();
      for (String myResult : result) {
        String[] parts = myResult.split("\",\"");
        String[] countyName_before =
            parts[0].substring(1).split(","); // Split the string at the comma
        String countyName =
            countyName_before[0].trim(); // Get the first part and remove leading/trailing spaces
        String countyCode = parts[2].replace("\"", "");
        countyCodeMap.put(countyName, countyCode);
      }
      county = county.replace("_", " ");
      return countyCodeMap.get(county);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}

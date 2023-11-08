package edu.brown.cs.student.main.Broadband;

/**
 * The `BroadbandData` record represents data related to broadband availability, specifically the
 * percentage of households with Wi-Fi access. This record is primarily intended for testing
 * purposes, providing a simple structure to encapsulate broadband data for use in test scenarios.
 */
public record BroadbandData(double wifiPercentage) {

  /**
   * Creates a new instance of `BroadbandData` with the specified Wi-Fi percentage.
   *
   * @param wifiPercentage The percentage of households with Wi-Fi access.
   */
  public BroadbandData {
    // Constructor Implementation...
  }

  /**
   * Retrieves the percentage of households with Wi-Fi access.
   *
   * @return The Wi-Fi percentage.
   */
  public double getWifiPercentage() {
    return wifiPercentage;
  }
}

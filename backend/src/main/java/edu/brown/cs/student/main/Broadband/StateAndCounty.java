package edu.brown.cs.student.main.Broadband;

/**
 * The `StateAndCounty` record represents a combination of a state and a county. It is used to
 * encapsulate information about a specific geographical location.
 */
public record StateAndCounty(String state, String county) {

  /**
   * Converts the `StateAndCounty` object into a string format suitable for sending as parameters to
   * a server.
   *
   * @return A string in the format "State=<state>&County=<county>".
   */
  public String toOurServerParams() {
    return "State=" + state + "&County=" + county;
  }
}

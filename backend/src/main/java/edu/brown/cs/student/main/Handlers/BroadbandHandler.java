package edu.brown.cs.student.main.Handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Broadband.BroadbandDataSource;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * The `BroadbandHandler` class handles HTTP requests related to broadband data in a web
 * application. It implements the Spark `Route` interface, enabling it to process incoming HTTP GET
 * requests. This class interacts with a `BroadbandDataSource` to retrieve information about
 * broadband availability in a specific state and county. It then generates JSON responses
 * containing the broadband data, including the state, county, household percentage, and the date
 * and time of access.
 */
public class BroadbandHandler implements Route {

  private Date date;
  private final BroadbandDataSource source;

  public BroadbandHandler(BroadbandDataSource source) {
    this.source = source;
  }

  /**
   * Handles the incoming HTTP GET request for broadband data. It retrieves state and county
   * information from the request parameters, fetches broadband data from a data source, and
   * responds with a JSON message containing the broadband details.
   *
   * @param request The HTTP request object.
   * @param response The HTTP response object.
   * @return A JSON response containing broadband data, or an error message if there's an issue.
   * @throws Exception If there are any exceptions during request handling.
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    try {
      String state = request.queryParams("State");
      String stateCode = this.source.getStateCode(state);
      String county = request.queryParams("County");
      String countyCode = this.source.getCountyCode(stateCode, county);
      String percentage = this.source.getHouseholdPercentage(stateCode, countyCode);
      this.date = Calendar.getInstance().getTime();
      DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
      String strDate = dateFormat.format(date);

      return new SuccessResponse(state, county, percentage, strDate).serialize();
    } catch (RuntimeException e) {

      return new FailureResponse().serialize();
    }
  }

  public record FailureResponse() {
    /**
     * Serializes the failure response as JSON.
     *
     * @return A JSON representation of the failure response.
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
      JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
      Map<String, Object> responseMap = new HashMap<>();
      responseMap.put("result", "failure");
      responseMap.put("error_type", "incorrect query format");
      return adapter.toJson(responseMap);
    }
  }

  /**
   * The `SuccessResponse` record represents a JSON response indicating a successful broadband data
   * retrieval operation. It includes methods for serializing the response as JSON.
   */
  public record SuccessResponse(String state, String county, String percentage, String date) {

    /**
     * @return this response, serialized as Json
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
      JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
      Map<String, Object> responseMap = new HashMap<>();
      try {
        responseMap.put("state", state);
        responseMap.put("county", county.replace("_", " "));
        responseMap.put("result", "success");
        responseMap.put("householdPercentage", percentage);
        responseMap.put("dateAndTimeAccessed", this.date);
        return adapter.toJson(responseMap);
      } catch (Exception e) {

        responseMap.put("type", "error");
        responseMap.put("error_type", "error_bad_json");
        responseMap.put("details", e.getMessage());
        return adapter.toJson(responseMap);
      }
    }
  }
}

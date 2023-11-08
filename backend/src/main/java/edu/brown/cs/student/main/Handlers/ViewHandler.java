package edu.brown.cs.student.main.Handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSV.CSVData;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * The `ViewHandler` class is responsible for handling HTTP requests related to viewing CSV data
 * within a web application. It implements the Spark `Route` interface, allowing it to process
 * incoming HTTP GET requests. This class retrieves the file path from a `CSVData` object, loads and
 * parses the CSV file, and generates JSON responses containing the parsed CSV data or error
 * information.
 */
public class ViewHandler implements Route {
  private String filepath;
  private CSVData data;

  public ViewHandler(CSVData data) {
    this.data = data;
  }

  /**
   * Handles the incoming HTTP GET request to view CSV data. It retrieves the file path from the
   * associated `CSVData` object, loads and parses the CSV file, and responds with a JSON message
   * containing the parsed data or error information.
   *
   * @param request The HTTP request object.
   * @param response The HTTP response object.
   * @return A JSON response containing either the parsed CSV data or an error message.
   * @throws Exception If there are any exceptions during request handling.
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    this.filepath = this.data.getFilePath();
    if (this.filepath == null || this.filepath.isEmpty()) {
      HashMap<String, Object> failure = new HashMap<>();
      failure.put("result: ", "error_bad_request: " + this.filepath);
      return new FailureResponse(failure).serialize();
    }
    // Load the CSV file and parse its contents using the Parser class
    List<List<String>> csvData = this.data.parseCSV(this.filepath);
    // Create a success response containing the CSV data
    return new SuccessResponse(csvData).serialize();
  }

  /**
   * The `SuccessResponse` record represents a JSON response indicating a successful CSV data
   * viewing operation. It includes methods for serializing the response as JSON.
   */
  public record SuccessResponse(List<List<String>> csvData) {
    /**
     * Serializes the success response as JSON.
     *
     * @return A JSON representation of the success response.
     */
    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
      JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
      Map<String, Object> responseMap = new HashMap<>();
      try {
        responseMap.put("data", csvData);
        responseMap.put("result", "success");
        return adapter.toJson(responseMap);
      } catch (Exception e) {
        // spark java will not recognize exception, so we have to catch it
        // too narrow, anything broad that spark will catch is acceptable
        responseMap.put("type", "error");
        responseMap.put("error_type", "error_bad_json");
        responseMap.put("details", e.getMessage());
        return adapter.toJson(responseMap);
      }
    }
  }

  /**
   * The `FailureResponse` record represents a JSON response indicating a failed CSV data viewing
   * operation. It includes methods for serializing the response as JSON.
   */
  public record FailureResponse(Map<String, Object> failure) {
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
      responseMap.put("error_type", "error_data_source");
      return adapter.toJson(responseMap);
    }
  }
}

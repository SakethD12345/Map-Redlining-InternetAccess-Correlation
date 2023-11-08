package edu.brown.cs.student.main.Handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSV.CSVData;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * The `LoadHandler` class is responsible for handling HTTP requests related to loading CSV data in
 * a web application. It implements the Spark `Route` interface, allowing it to process incoming
 * HTTP GET requests. This class extracts the file path from the request parameters, sets it in a
 * `CSVData` object, and generates JSON responses indicating success or failure.
 */
public class LoadHandler implements Route {
  //  private Server server;
  private String filepath;
  private CSVData data;

  public LoadHandler(CSVData data) {
    this.data = data;
  }

  /**
   * Handles the incoming HTTP GET request to load CSV data. It extracts the file path from the
   * request parameters, sets it in the `CSVData` object, and responds with a JSON message
   * indicating success or failure.
   *
   * @param request The HTTP request object.
   * @param response The HTTP response object.
   * @return A JSON response indicating the success or failure of the CSV data loading request.
   * @throws Exception If there are any exceptions during request handling.
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    this.filepath = request.queryParams("filepath");
    this.data.setFilePath(filepath);
    if (filepath == null || filepath.isEmpty()) {
      return new FailureResponse(this.filepath).serialize();
    }
    Path path = Paths.get(filepath);
    if (!Files.exists(path) || !Files.isReadable(path)) {
      return new FailureResponse(this.filepath).serialize();
    }

    return new SuccessResponse(this.filepath).serialize();
  }

  /**
   * The `SuccessResponse` record represents a JSON response indicating a successful CSV data
   * loading operation. It includes methods for serializing the response as JSON.
   */
  public record SuccessResponse(String success) {
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
        responseMap.put("filepath", success);
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
   * The `FailureResponse` record represents a JSON response indicating a failed CSV data loading
   * operation, typically due to missing or empty file information. It includes methods for
   * serializing the response as JSON.
   */
  public record FailureResponse(String filepath) {

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
      responseMap.put("error_type", "error_bad_request");
      responseMap.put("details", "missing or empty file.");
      return adapter.toJson(responseMap);
    }
  }
}

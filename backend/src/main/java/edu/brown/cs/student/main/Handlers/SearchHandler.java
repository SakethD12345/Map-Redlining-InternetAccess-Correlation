package edu.brown.cs.student.main.Handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.CSV.CSVData;
import edu.brown.cs.student.main.CSV.Searcher;
import edu.brown.cs.student.main.Exceptions.FactoryFailureException;
import edu.brown.cs.student.main.Handlers.BroadbandHandler.FailureResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * The `SearchHandler` class is responsible for handling HTTP requests related to searching within
 * CSV data in a web application. It implements the Spark `Route` interface, allowing it to process
 * incoming HTTP GET requests. This class retrieves the file path from a `CSVData` object, performs
 * searches within the CSV data based on specified criteria (e.g., value, headers, column name or
 * index), and generates JSON responses containing the search results or error information.
 */
public class SearchHandler implements Route {
  private CSVData data;
  private String filepath;

  public SearchHandler(CSVData data) {
    this.data = data;
  }

  /**
   * Handles the incoming HTTP GET request for searching within CSV data. It retrieves the file path
   * from the associated `CSVData` object, performs the search based on request parameters, and
   * responds with a JSON message containing the search results or an error message.
   *
   * @param request The HTTP request object.
   * @param response The HTTP response object.
   * @return A JSON response containing either the search results or an error message.
   * @throws IOException If there are any I/O errors during the search operation.
   * @throws FactoryFailureException If there are any failures related to factory operations.
   */
  public Object handle(Request request, Response response)
      throws IOException, FactoryFailureException {
    this.filepath = data.getFilePath();
    //Path path = Paths.get((filepath));
    if (this.filepath == null || this.filepath.isEmpty()) {
      return new FailureResponse().serialize();
    }


    String value = request.queryParams("Value");
    String headers = request.queryParams("Headers");
    try {
      if (headers.equalsIgnoreCase("no")) {
        Searcher searcher = new Searcher(this.filepath, value);
        List<List<String>> result = searcher.getNoHeaderResult();
        return new SearchHandler.SuccessResponse(result).serialize();
      }
    }catch (RuntimeException e) {
      return new SearchHandler.FailureResponse().serialize();
    }


    String nameOrIndex = request.queryParams("NameOrIndex");
    try {
      if (headers.equalsIgnoreCase("yes")) {
        if (nameOrIndex.equalsIgnoreCase("name")) {
          String name = request.queryParams("Name");
          Searcher searcher = new Searcher(this.filepath, value, name);
          List<List<String>> result = searcher.getColumnNameResult();
          return new SearchHandler.SuccessResponse(result).serialize();
        } else if (nameOrIndex.equalsIgnoreCase("index")) {
          int index = Integer.parseInt(request.queryParams("Index"));
          Searcher searcher = new Searcher(this.filepath, value, index);
          List<List<String>> result = searcher.getColumnNumberResult();
          return new SearchHandler.SuccessResponse(result).serialize();
        }
      }
    }catch (RuntimeException e) {

      return new SearchHandler.FailureResponse().serialize();
    }

    return new SearchHandler.FailureResponse().serialize();
  }

  /**
   * The `SuccessResponse` record represents a JSON response indicating a successful search
   * operation within CSV data. It includes methods for serializing the response as JSON.
   */
  public record SuccessResponse(List<List<String>> data) {
    /**
     * Serializes the success response as JSON.
     *
     * @return A JSON representation of the success response.
     */
    String serialize() {
      try {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<SearchHandler.SuccessResponse> adapter =
            moshi.adapter(SearchHandler.SuccessResponse.class);
        return adapter.toJson(this);
      } catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
    }
  }

  /**
   * The `FailureResponse` record represents a JSON response indicating a failed search operation
   * within CSV data. It includes methods for serializing the response as JSON.
   */
  public record FailureResponse() {

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
      responseMap.put("result", "failure");
      responseMap.put("error_type", "incorrect query format");
      return adapter.toJson(responseMap);
    }
  }
}


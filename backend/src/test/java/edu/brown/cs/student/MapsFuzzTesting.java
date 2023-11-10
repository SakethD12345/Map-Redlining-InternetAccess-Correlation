package edu.brown.cs.student;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Handlers.CacheProxy;
import edu.brown.cs.student.main.Handlers.JSONHandler;
import edu.brown.cs.student.main.Handlers.MapsHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.AssertJUnit;
import spark.Spark;

/**
 * This class provides a fuzz test for our json handler
 */
public class MapsFuzzTesting {

  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;
  @BeforeAll
  public static void setup_before_everything() {
    // arbitrary available port.
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING); // empty name = root logger
  }

  /**
   * Shared state for all tests. We need to be able to mutate it (adding recipes etc.) but never need to replace
   * the reference itself. We clear this state out after every test runs.
   */

  @BeforeEach
  public void setup() {
    Spark.get("areaSearch", new MapsHandler());
    Spark.get("geoJSON", new JSONHandler(new CacheProxy(10,10)));
    Spark.init();
    Spark.awaitInitialization();

    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
    Spark.unmap("areaSearch");
    Spark.unmap("geoJSON");
    Spark.awaitStop(); // don't proceed until the server is stopped
  }

  private static HttpURLConnection tryRequest(String apiCall) throws IOException {
    // Configure the connection (but don't actually send the request yet)
    URL requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall);
    HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();

    // The default method is "GET", which is what we're using here.
    // If we were using "POST", we'd need to say so.
    // clientConnection.setRequestMethod("GET");

    clientConnection.connect();
    return clientConnection;
  }

  /**
   * This fuzz test sends 100 different combinations of coordiantes to our server
   * and makes sure a success response is returned for each one
   * @throws IOException
   */
  @Test
  public void testRandomBounds() throws IOException {
    // Longitude and latitude for all of the US
    final double latRangeMin = -90;
    final double latRangeMax = 90;
    final double lonRangeMin = -180;
    final double lonRangeMax = 180;
    final int MAX_RANDOM_TESTS = 100;

    Random r = new Random();

    for (int intTest = 0; intTest < MAX_RANDOM_TESTS; intTest++) {
      double randomLatValue1 = latRangeMin + (latRangeMax - latRangeMin) * r.nextDouble();
      double randomLatValue2 = latRangeMin + (latRangeMax - latRangeMin) * r.nextDouble();

      double randomLonValue1 = lonRangeMin + (lonRangeMax - lonRangeMin) * r.nextDouble();
      double randomLonValue2 = lonRangeMin + (lonRangeMax - lonRangeMin) * r.nextDouble();

      double latMin = Math.min(randomLatValue1, randomLatValue2);
      double latMax = Math.max(randomLatValue1, randomLatValue2);
      double lonMin = Math.min(randomLonValue1, randomLonValue2);
      double lonMax = Math.max(randomLonValue1, randomLonValue2);

      HttpURLConnection loadConnection = tryRequest("geoJSON?minLat=" + latMin + "&maxLat=" + latMax + "&minLong=" + lonMin + "&maxLong=" + lonMax);
      Map<String, Object> body =
          adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
      AssertJUnit.assertEquals(200, loadConnection.getResponseCode());
      assertEquals("FeatureCollection",body.get("type"));
      assertEquals("success",body.get("return Type"));
    }
  }
}



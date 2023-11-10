package edu.brown.cs.student;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Handlers.CacheProxy;
import edu.brown.cs.student.main.Handlers.JSONHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.AssertJUnit;
import spark.Spark;

/**
 * This is the test class for when a user searches for a specific latitude
 * or longitude bound. We are mostly error testing in this class as we fuzz
 * tested which handles several working cases.
 */
public class MapsJSONTesting {


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
   * Shared state for all tests. We need to be able to mutate it (adding recipes etc.) but never
   * need to replace the reference itself. We clear this state out after every test runs.
   */

  @BeforeEach
  public void setup() {
    Spark.get("geoJSON", new JSONHandler(new CacheProxy(10, 10)));
    Spark.init();
    Spark.awaitInitialization();

    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
  }

  @AfterEach
  public void teardown() {
    // Gracefully stop Spark listening on both endpoints
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
   * This tests the correct error response is returned if there
   * are no lat and long queries
   * @throws IOException
   */
  @Test
  public void testNoLatLongParameters() throws IOException {
    HttpURLConnection loadConnection = tryRequest("geoJSON?efjkwefnew");
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(200, loadConnection.getResponseCode());
    assertEquals("Search query must include: 'minLat', 'maxLat', 'minLong', and 'maxLong'",
        body.get("error_description"));
    assertEquals("failure", body.get("result"));
    assertEquals("incorrect query format", body.get("error_type"));
  }

  /**
   * This nmethod tests if no bounds are entered then the whole dataset gets returned
   *
   * @throws IOException
   */
  @Test
  public void testEmptyLatLongBounds() throws IOException {
    HttpURLConnection loadConnection = tryRequest("geoJSON?minLat=&maxLat=&minLong=&maxLong=");
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(200, loadConnection.getResponseCode());
    assertEquals("FeatureCollection", body.get("type"));
    assertEquals("success", body.get("return Type"));
  }

  /**
   * This method tests if there is no int for one or more of the
   * lat long queries
   * @throws IOException
   */
  @Test
  public void oneOrMoreEmptyLatLong() throws IOException {
    HttpURLConnection loadConnection = tryRequest(
        "geoJSON?minLat=45&maxLat=55&minLong=110&maxLong=");
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(200, loadConnection.getResponseCode());
    assertEquals("FeatureCollection", body.get("type"));
    assertEquals("success", body.get("return Type"));
    assertTrue(
        body.get("features").toString().contains("{area_description_data={1=Although this area "
            + "stretches over considerable territory, covering nearly all of the southeast portion of the city, it is very properly a \"still desirable\" residential section. The substantial development of the last thirty years has been through this strip to the south-eastern part of the city. Houses are the cottage and bungalow type, many of them practically new and ranging in age up to 25 years. The \"average\" type of business and professional people live in this area. Its only detrimental influence is that the western one-half of it lies in t"
            + "he \"smoke belt\". At the southern parts of the area are two golf courses. The areas around Liberty Park are, in appearance, very good. There has been very little business or apartment house encroachment in this section."));
  }

  /**
   * This method tests a random set of ints for lat and long works
   * @throws IOException
   */
  @Test
  public void randomWorkingTest() throws IOException {
    HttpURLConnection loadConnection = tryRequest(
        "geoJSON?minLat=45&maxLat=55&minLong=110&maxLong=34");
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(200, loadConnection.getResponseCode());
    assertEquals("FeatureCollection", body.get("type"));
    assertEquals("success", body.get("return Type"));
  }

  @Test
  public void testInvalidLatMin() throws IOException {
    HttpURLConnection loadConnection = tryRequest(
        "geoJSON?minLat=45&maxLat=55&minLong=110&maxLong=YOU");
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    assertEquals(200, loadConnection.getResponseCode());
  }

}

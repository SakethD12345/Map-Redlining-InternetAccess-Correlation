package edu.brown.cs.student;

import static org.testng.AssertJUnit.assertEquals;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Broadband.BroadbandData;
import edu.brown.cs.student.main.Broadband.BroadbandDataSource;
import edu.brown.cs.student.main.Broadband.RealBBSource;
import edu.brown.cs.student.main.Broadband.StateAndCounty;
import edu.brown.cs.student.main.CSV.CSVData;
import edu.brown.cs.student.main.Handlers.BroadbandHandler;
import edu.brown.cs.student.main.Handlers.LoadHandler;
import edu.brown.cs.student.main.Handlers.SearchHandler;
import edu.brown.cs.student.main.Handlers.ViewHandler;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

public class Testing {
  @BeforeAll
  public static void setup_before_everything() {
    Spark.port(0);
    Logger.getLogger("").setLevel(Level.WARNING);
  }

  private final Type mapStringObject =
      Types.newParameterizedType(Map.class, String.class, Object.class);
  private JsonAdapter<Map<String, Object>> adapter;
  private JsonAdapter<BroadbandData> BroadBandDataAdapter;

  @BeforeEach
  public void setup() {
    // Re-initialize parser, state, etc. for every test method
    CSVData data =
        new CSVData(
            "/Users/shadisoufan/Desktop/server-annie-ye4-ShadiSoufan44/data/state-codes/codes.csv");
    CSVData data2 =
        new CSVData(
            "/Users/shadisoufan/Desktop/server-annie-ye4-ShadiSoufan44/data/census/dol_ri_earnings_disparity.csv");
    BroadbandDataSource realSource = new RealBBSource();
    Spark.get("/broadband", new BroadbandHandler(realSource));
    Spark.get(("/loadCSV"), new LoadHandler(data));
    Spark.get(("/viewCSV"), new ViewHandler(data));
    Spark.get(("/searchCSV"), new SearchHandler(data2));
    Spark.awaitInitialization(); // don't continue until the server is listening

    Moshi moshi = new Moshi.Builder().build();
    adapter = moshi.adapter(mapStringObject);
    BroadBandDataAdapter = moshi.adapter(BroadbandData.class);
  }

  @AfterEach
  public void teardown() {
    Spark.unmap("/broadband");
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

  final StateAndCounty TexasHarris = new StateAndCounty("Texas", "Harris_County");

  /**
   * This test uses our Mocked Broadbandata source to test our BroadHandler without actually using
   * the ACS's Census API. It makes sure that our integration is correct and that can return an OK
   * response code of 200.
   */
  @Test
  public void testMockSource() throws IOException {
    Spark.unmap("broadband");
    BroadbandDataSource mockedSource = new MockedBBSource(new BroadbandData(87.3));
    Spark.get("/broadband", new BroadbandHandler(mockedSource));

    // Get an OK response (the *connection* worked, the *API* provides an error response)
    HttpURLConnection loadConnection = tryRequest("broadband?" + TexasHarris.toOurServerParams());
    assertEquals(200, loadConnection.getResponseCode());
    // Get the expected response: a success

    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    showDetailsIfError(body);
    body.put("household percentage", "{\"wifiPercentage\":87.3}");
    assertEquals("success", body.get("result"));

    assertEquals(
        BroadBandDataAdapter.toJson(new BroadbandData(87.3)), body.get("household percentage"));
    loadConnection.disconnect();
  }

  /**
   * Tests that the BroadHandler works ( returns an OK response code of 200) and ensures that
   * whatever State & County combo the user's query includes, the correct percentage of how many
   * people in that county have Broadband(WiFI) access. Here we look at Bexar County in Texas with
   * 87.6% broadband.
   *
   * @throws IOException
   */
  @Test
  public void testBroadBandAndBroadHandler() throws IOException {
    HttpURLConnection loadConnection = tryRequest("broadband?State=Texas&County=Bexar_County");
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    showDetailsIfError(body);
    assertEquals(200, loadConnection.getResponseCode());
    assertEquals("87.6", body.get("household percentage").toString());
  }

  /**
   * Tests that the appropriate failure response is returned when an invalid query is passed into
   * the broadband endpoint.
   *
   * @throws IOException
   */
  @Test
  public void testBroadBandFailure() throws IOException {
    HttpURLConnection loadConnection = tryRequest("broadband?State=Texs&County=Harris_County");
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    showDetailsIfError(body);
    assertEquals("failure", body.get("result"));
  }

  /**
   * Tests that the LoadHandler works, ( returns an OK response code of 200), that load CSV
   * correctly loads and stores the CSV data, and that the correct message is given to the user.
   *
   * @throws IOException
   */
  @Test
  public void testLoadCSVAndLoadHandler() throws IOException {
    HttpURLConnection loadConnection =
        tryRequest(
            "loadCSV?filepath=/Users/shadisoufan/Desktop/server-annie-ye4-ShadiSoufan44/data/state-codes/codes.csv");
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    showDetailsIfError(body);
    assertEquals(200, loadConnection.getResponseCode());
    assertEquals(
        "{result=success, filepath=/Users/shadisoufan/Desktop/server-annie-ye4-ShadiSoufan44/data/state-codes/codes.csv}",
        body.toString());
  }

  /**
   * Tests that the ViewHandler works, ( returns an OK response code of 200), that the viewCSV
   * method correctly parses the data/file, and that the contents of the file are available to the
   * user.
   *
   * @throws IOException
   */
  @Test
  public void testViewCsvAndViewHandler() throws IOException {
    tryRequest(
        "loadCSV?filepath=/Users/shadisoufan/Desktop/server-annie-ye4-ShadiSoufan44/data/state-codes/codes.csv");
    HttpURLConnection loadConnection = tryRequest("viewCSV");
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    showDetailsIfError(body);
    assertEquals(200, loadConnection.getResponseCode());
    assertEquals(
        "{result=success, data=[[Alabama, 01], [Alaska, 02], [Arizona, 04], [Arkansas, 05], [California, 06], [Louisiana, 22], [Kentucky, 21], [Colorado, 08], [Connecticut, 09], [Delaware, 10], [District of Columbia, 11], [Florida, 12], [Georgia, 13], [Hawaii, 15]], [Idaho, 16]], [Illinois, 17]], [Indiana, 18]], [Iowa, 19]], [Kansas, 20]], [Maine, 23]], [Maryland, 24]], [Massachusetts, 25]], [Michigan, 26]], [Minnesota, 27]], [Mississippi, 28]], [Missouri, 29]], [Montana, 30]], [Nebraska, 31]], [Nevada, 32]], [New Hampshire, 33]], [New Jersey, 34]], [New Mexico, 35]], [New York, 36]], [North Carolina, 37]], [North Dakota, 38]], [Ohio, 39]], [Oklahoma, 40]], [Oregon, 41]], [Pennsylvania, 42]], [Rhode Island, 44]], [South Carolina, 45]], [South Dakota, 46]], [Tennessee, 47]], [Texas, 48]], [Utah, 49]], [Vermont, 50]], [Virginia, 51]], [Washington, 53]], [West Virginia, 54]], [Wisconsin, 55]], [Wyoming, 56]], [Puerto Rico, 72]]]]}",
        body.toString());
  }

  /**
   * Tests that the SearchHandler works, ( returns an OK response code of 200), that the searchCSV
   * method/input correctly returns the data in response to the user's query. This test searches
   * using no headers.
   */
  @Test
  public void testSearchCsvNoHeadersAndSearchHandler() throws IOException {
    tryRequest(
        "loadCSV?filepath=/Users/shadisoufan/Desktop/server-annie-ye4-ShadiSoufan44/data/census/dol_ri_earnings_disparity.csv");
    HttpURLConnection loadConnection = tryRequest("searchCSV?Value=White&Headers=no");
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    showDetailsIfError(body);
    assertEquals(200, loadConnection.getResponseCode());
    assertEquals("{data=[[RI, White,  $1,058.47 , 395773.6521,  $1.00 , 75%]]}", body.toString());
  }

  /**
   * Tests that the SearchHandler works, ( returns an OK response code of 200), that the searchCSV
   * method/input correctly returns the data in response to the user's query. This test searches
   * using headers, more specifically header names.
   */
  @Test
  public void testSearchCsvHeaderNameAndSearchHandler() throws IOException {
    tryRequest(
        "loadCSV?filepath=/Users/shadisoufan/Desktop/server-annie-ye4-ShadiSoufan44/data/census/dol_ri_earnings_disparity.csv");
    HttpURLConnection loadConnection =
        tryRequest("searchCSV?Value=Black&Headers=yes&NameOrIndex=name&Name=Data%20type");
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    showDetailsIfError(body);
    assertEquals(200, loadConnection.getResponseCode());
    assertEquals(
        "{data=[[RI, Black,  $770.26 , 30424.80376,  $0.73 , 6%], [RI, Black,  $310.26 , 30424.83376,  $0.42 , 2%]]}",
        body.toString());
  }

  /**
   * Tests that the SearchHandler works, ( returns an OK response code of 200), that the searchCSV
   * method/input correctly returns the data in response to the user's query. This test searches
   * using headers, more specifically header indexes.
   */
  @Test
  public void testSearchCsvIndexAndSearchHandler() throws IOException {
    tryRequest(
        "loadCSV?filepath=/Users/shadisoufan/Desktop/server-annie-ye4-ShadiSoufan44/data/census/dol_ri_earnings_disparity.csv");
    HttpURLConnection loadConnection =
        tryRequest("searchCSV?Value=Black&Headers=yes&NameOrIndex=index&Index=1");
    Map<String, Object> body =
        adapter.fromJson(new Buffer().readFrom(loadConnection.getInputStream()));
    showDetailsIfError(body);
    assertEquals(200, loadConnection.getResponseCode());
    assertEquals(
        "{data=[[RI, Black,  $770.26 , 30424.80376,  $0.73 , 6%], [RI, Black,  $310.26 , 30424.83376,  $0.42 , 2%]]}",
        body.toString());
  }

  private void showDetailsIfError(Map<String, Object> body) {
    if (body.containsKey("type") && "error".equals(body.get("type"))) {
      System.out.println(body.toString());
    }
  }

  /** Tests that the appropriate state code is returned. */
  @Test
  public void getStateCode() {
    RealBBSource source = new RealBBSource();
    String californiaCode = source.getStateCode("California");
    assertEquals("06", californiaCode);
  }

  /** Tests that the appropriate county code is returned. */
  @Test
  public void getCountyCode() {
    RealBBSource source = new RealBBSource();
    String sanFranciscoCode = source.getCountyCode("06", "San Francisco County");
    assertEquals("075", sanFranciscoCode);
  }

  /** Tests that the appropriate household percentage is returned. */
  @Test
  public void getHouseholdPercentage() {
    RealBBSource source = new RealBBSource();
    String percentage = source.getHouseholdPercentage("06", "075");
    assertEquals("87.1", percentage);

    String stateCode = source.getStateCode("Florida");
    String countyCode = source.getCountyCode(stateCode, "Monroe County");
    String floridaPercentage = source.getHouseholdPercentage(stateCode, countyCode);
    assertEquals("83.0", floridaPercentage);
  }
}

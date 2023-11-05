import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory;
import handler.SearchHandler;
import handler.LoadHandler;
import handler.ViewHandler;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import spark.Spark;


import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static spark.Spark.after;

/**
 * This class tests the different handler methods
 */
public class TestCSVHandler {

    /**
     * This method sets up the port number and the logger
     */
    @BeforeAll
    public static void setup_before_everything() {
        Spark.port(4567);
        Logger.getLogger("").setLevel(Level.WARNING);
    }

    /**
     * This method helps set up the url pathways
     */
    @BeforeEach
    public void setup() {
        Spark.get("/loadcsv", new LoadHandler());
        Spark.get("/searchcsv", new SearchHandler());
        Spark.get("/viewcsv", new ViewHandler());
        Spark.init();
        Spark.awaitInitialization();
    }

    /**
     * This method is for tearing down the pathways
     */
    @AfterEach
    public void teardown() {
        Spark.unmap("/loadcsv");
        Spark.unmap("/searchcsv");
        Spark.unmap("/viewcsv");
        Spark.awaitStop();
    }

    /**
     * This method helps create the url's connection with three parameters
     * @param apiCall
     * @param query1
     * @param query2
     * @return the connection
     * @throws IOException
     */
    static private HttpURLConnection tryRequest(String apiCall, String query1, String query2) throws IOException {
        URL requestURL = new URL("http://localhost:" + Spark.port());

        if (apiCall.equals("loadcsv")) {
            requestURL = new URL("http://localhost:" + Spark.port() + "/" + apiCall + "?filename=" + query1 + "&header=" + query2);
        }
        else if (apiCall.equals("searchcsv")) {
            requestURL = new URL("http://localhost:"+Spark.port()+"/"+apiCall + "?target=" + query1 + "&identifier=" + query2);
        }
        HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
        clientConnection.connect();
        return clientConnection;
    }

    /**
     * This method helps set up the url's connection with two parameters
     * @param apiCall
     * @param target
     * @return the connection
     * @throws IOException
     */
    static private HttpURLConnection tryRequest(String apiCall, String target) throws IOException {
        URL requestURL = new URL("http://localhost:"+Spark.port()+"/"+apiCall + "?target=" + target);
        HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
        clientConnection.connect();
        return clientConnection;
    }

    /**
     * This method helps set up the url's connection with one parameter
     * @param apiCall
     * @return the connection
     * @throws IOException
     */
    static private HttpURLConnection tryRequest(String apiCall) throws IOException {
        URL requestURL = new URL("http://localhost:"+Spark.port()+"/"+apiCall);
        HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
        clientConnection.connect();
        return clientConnection;
    }

    /**
     * This method tests to see if the csv loaded can be successful
     */
    @Test
    public void testSuccessfulLoadCSV() {
        Boolean exceptionThrown = Boolean.FALSE;
        try {
            String filename = "/Users/lanayang-maccini/Desktop/CSCI0320/server-ssdhulip-lyangmaccini/data/data1.csv";
            HttpURLConnection clientConnection = tryRequest("loadcsv", filename, "true");
            assertEquals(200, clientConnection.getResponseCode());

            Moshi moshi = new Moshi.Builder().build();
            Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
            JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

            Map<String, Object> response = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
            clientConnection.disconnect();
            assertEquals(response.get("filepath"), filename);
        }
        catch (IOException e) {
            exceptionThrown = Boolean.TRUE;
        }
        assertFalse(exceptionThrown); // making sure no exceptions were thrown in process
    }

    /**
     * This method tests to see if the csv loading can fail and is properly error checked
     */
    @Test
    public void testFailureLoadCSV() {
        Boolean exceptionThrown = Boolean.FALSE;
        try {
            String filename = "hello";
            HttpURLConnection clientConnection = tryRequest("loadcsv", filename, "true");
            assertEquals(200, clientConnection.getResponseCode());

            Moshi moshi = new Moshi.Builder().build();
            Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
            JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

            Map<String, Object> response = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
            clientConnection.disconnect();
            assertEquals(response.get("result"), "error_datasource");
        }
        catch (IOException e) {
            exceptionThrown = Boolean.TRUE; // the actual API connection was successful, but the overall query was a failure because the file doesn't exist, which is the parser's job to throw an erorr for.
        }
        assertFalse(exceptionThrown);
    }

    /**
     * This method tests if the seraching of the csv works properly
     */
    @Test
    public void testSearchCSV() {
        Boolean exceptionThrown = Boolean.FALSE;
        try {
            HttpURLConnection loadConnection = tryRequest("loadcsv", "/Users/lanayang-maccini/Desktop/CSCI0320/server-ssdhulip-lyangmaccini/data/data1.csv", "true");
            assertEquals(200, loadConnection.getResponseCode());
            loadConnection.disconnect();
            String target = "cranston";
            HttpURLConnection clientConnection1 = tryRequest("searchcsv", target, "0");
            assertEquals(200, clientConnection1.getResponseCode());

            Moshi moshi = new Moshi.Builder().build();
            Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
            JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

            Map<String, Object> response = adapter.fromJson(new Buffer().readFrom(clientConnection1.getInputStream()));
            clientConnection1.disconnect();
            assertEquals(response.get("target"), target); // test that normal search went through
            ArrayList<List<String>> searchedRows = new ArrayList<>();
            String[] row1 = {"Cranston", "77145.00", "95763.00", "38269.00"};
            searchedRows.add(Arrays.asList(row1));
            assertEquals(response.get("data"), searchedRows); // testing the contents of normal search

            String target2 = "400";
            HttpURLConnection clientConnection2 = tryRequest("searchcsv", target2, "0");
            Map<String, Object> response2 = adapter.fromJson(new Buffer().readFrom(clientConnection2.getInputStream()));
            clientConnection2.disconnect();

            assertEquals(response2.get("target"), target2);
            assertEquals(response2.get("data"), new ArrayList<>()); // testing search with no results

            HttpURLConnection loadConnection2 = tryRequest("loadcsv", "/Users/lanayang-maccini/Desktop/CSCI0320/server-ssdhulip-lyangmaccini/data/data2.csv", "true");
            assertEquals(200, loadConnection2.getResponseCode());
            loadConnection2.disconnect();

            HttpURLConnection clientConnection3 = tryRequest("searchcsv", target2);
            Map<String, Object> response3 = adapter.fromJson(new Buffer().readFrom(clientConnection3.getInputStream()));
            clientConnection3.disconnect();

            ArrayList<List<String>> searchedRows2 = new ArrayList<>();

            String[] row2 = {"Keeney", "400", "238", "South"};
            String[] row3 = {"Miller", "400", "275", "North"};
            searchedRows2.add(Arrays.asList(row2));
            searchedRows2.add(Arrays.asList(row3));
            assertEquals(response3.get("data"), searchedRows2); // testing search after reloading file
        } catch (IOException e) {
            exceptionThrown = Boolean.TRUE;
        }
        assertFalse(exceptionThrown);
    }

    /**
     * This method tests to see if the view handler class works properly
     */
    @Test
    public void testViewCSV() {
        Boolean exceptionThrown = Boolean.FALSE;
        try {
            Moshi moshi = new Moshi.Builder().build();
            Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
            JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

            HttpURLConnection load1Connection = tryRequest("loadcsv", "/Users/lanayang-maccini/Desktop/CSCI0320/server-ssdhulip-lyangmaccini/data/data1.csv", "true");
            load1Connection.disconnect();

            HttpURLConnection clientConnection1 = tryRequest("viewcsv");
            assertEquals(200, clientConnection1.getResponseCode());

            Map<String, Object> response = adapter.fromJson(new Buffer().readFrom(clientConnection1.getInputStream()));
            clientConnection1.disconnect();
            assertEquals(response.get("result"), "success");
        } catch (IOException e) {
            exceptionThrown = Boolean.TRUE;
        }
        assertFalse(exceptionThrown);
    }
    /**
     * These are new tests for testing jsonHandle
     */
    @Test
    public void testJsonHandler() throws IOException {
        String json = "{\"name\": \"Alice\", \"age\": 28}";
        LoadHandler loadHandler = new LoadHandler();
        Map<String, Object> jsonMap = loadHandler.handleJSON(json);
        assertEquals("Alice", jsonMap.get("name"));
    }
    /**
     * These are new tests for testing jsonHandle
     */
    @Test
    public void testJsonHandler2() throws IOException {
        String json = "{\"name\": \"Alice\", \"age\": 28}";
        LoadHandler loadHandler = new LoadHandler();
        Map<String, Object> jsonMap = loadHandler.handleJSON(json);
        jsonMap.put("city", "Wonderland");
        assertEquals("Wonderland", jsonMap.get("city"));
    }
}

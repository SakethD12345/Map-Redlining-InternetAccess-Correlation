import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import datasource.MockCensusDatasource;
import handler.BroadbandHandler;
import okio.Buffer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


import static org.junit.jupiter.api.Assertions.*;

/**
 * This class tests the broadband handler and the datasource class
 */
public class TestAPIHandler {
    private MockCensusDatasource mockedData;

    /**
     * This method sets up the port and logger
     */
    @BeforeAll
    public static void setup_before_everything() {
        Spark.port(4567);
        Logger.getLogger("").setLevel(Level.WARNING);
    }

    /**
     * This method sets up the pathway and initializes the mockedData variable
     */
    @BeforeEach
    public void setup() {
        this.mockedData = new MockCensusDatasource();
        Spark.get("/broadband", new BroadbandHandler(mockedData));
        Spark.init();
        Spark.awaitInitialization();
    }

    /**
     * This method tears down the pathway
     */
    @AfterEach
    public void teardown() {
        Spark.unmap("/broadband");
        Spark.awaitStop();
    }

    /**
     * This method forms a connection for the given url with the given parameters
     * @param apiCall
     * @param query1
     * @param query2
     * @return the connection
     * @throws IOException
     */
    static private HttpURLConnection tryRequest(String apiCall, String query1, String query2) throws IOException {
        URL requestURL = new URL("http://localhost:"+Spark.port()+"/"+apiCall + "?target=" + query1 + "&identifier=" + query2);
        HttpURLConnection clientConnection = (HttpURLConnection) requestURL.openConnection();
        clientConnection.connect();
        return clientConnection;
    }

    /**
     * This method tests to see if the BroadbandHandler class and its methods work properly
     */
    @Test
    public void testBroadbandHandler() {
        Boolean exceptionThrown = Boolean.FALSE;
        try {
            String state = "Rhode Island";
            String county = "Providence";
            HttpURLConnection clientConnection = tryRequest("broadband", this.mockedData.getStateCode(state),
                    this.mockedData.getCountyCode(county, state));
            assertEquals(200, clientConnection.getResponseCode());

            Moshi moshi = new Moshi.Builder().build();
            Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
            JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

            Map<String, Object> response = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
            clientConnection.disconnect();
            assertEquals(response.get("date and time"), "Monday");
            assertEquals(response.get("broadband"), "900");
        }
        catch (IOException e) {
            exceptionThrown = Boolean.TRUE;
        }
        assertFalse(exceptionThrown);
    }
}

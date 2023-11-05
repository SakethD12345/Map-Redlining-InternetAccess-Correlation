package datasource;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import okio.Buffer;
import server.Server;
import exception.DatasourceException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class implements the CensusDatasource class and its 4 corresponding methods.
 * The class creates the hashmap for converting the state names to codes along with the cache that stores state name to
 * county name to county code
 */
public class APICensusDatasource implements CensusDatasource {
    private static HashMap<String, String> stateCodes;
    private Cache<String, HashMap<String, String>> countyCache;
    private Boolean stateCodesGenerated;

    /**
     * The constructor builds the cache and defines its properties
     */
    public APICensusDatasource() {
        this.countyCache = CacheBuilder.newBuilder()
                .maximumSize(10)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
        this.stateCodesGenerated = Boolean.FALSE;
    }

    /**
     * This method takes in a state name as a string and returns the state code
     *
     * @param state name
     * @return the state code
     * @throws DatasourceException
     * @throws IOException
     */
    public String getStateCode(String state) throws DatasourceException, IOException {

        if (!this.stateCodesGenerated) {
            generateStateCodes();
            this.stateCodesGenerated = Boolean.TRUE;
        }
        // If the hashmap already contains the state then it gets the code associated with that name
        if (stateCodes.containsKey(state)) {
            return stateCodes.get(state);
        } else {
            throw new DatasourceException("Invalid state.");
        }

    }

    /**
     * This method takes in the county name and state name and returns the county code
     * @param county name
     * @param state name
     * @return the county code
     * @throws DatasourceException
     * @throws IOException
     */
    public String getCountyCode(String county, String state) throws DatasourceException, IOException {
        String countyCode;

        // If the cache has the state name in it then the cache gets the county code from the associated county name
        if (countyCache.asMap().containsKey(state)) {
            if (countyCache.asMap().get(state).containsKey(county + " County, " + state)) {
                countyCode = countyCache.getIfPresent(state).get(county + " County, " + state);
            } else {
                throw new DatasourceException("Invalid county.");
            }
        }
        // Otherwise the code adds the state's counties and their respective codes to the hashmap in the countyCache
        else {
            HashMap<String, String> countyMap = this.generateCountyCodes(state);
            String countyName = county + " County, " + state;
            if (countyMap.containsKey(countyName)) {
                countyCode = countyMap.get(countyName);
            }
            else {
                throw new DatasourceException("County not in given state.");
            }

        }
        return countyCode;
    }

    /**
     * This method uses the api census to generate a hashmap of state names to state codes
     * @throws DatasourceException
     * @throws IOException
     */
    private static void generateStateCodes() throws DatasourceException, IOException {
        URL requestURL = new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=state:*");
        HttpURLConnection clientConnection = connect(requestURL);
        Moshi moshi = new Moshi.Builder().build();
        Type listObject = Types.newParameterizedType(List.class, List.class, String.class);
        JsonAdapter<List<List<String>>> adapter = moshi.adapter(listObject);
        List<List<String>> body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
        clientConnection.disconnect();
        stateCodes = listToMap(body);
    }

    /**
     * This method takes in a List of list of Strings and converts it into a Hashmap where the first value of the list
     * is the key and the last value of the list is the value of the hashmap. This is to match the format of the api
     * census and where the name and code is located.
     * @param originalList is the original 2D list
     * @return the 2D list reformatted as a hashmap
     */
    private static HashMap<String, String> listToMap(List<List<String>> originalList) {
        HashMap<String, String> finalMap = new HashMap<>();
        for (List<String> row : originalList) {
            // The first value will be the state or county name and the last value is the respective code
            finalMap.put(row.get(0), row.get(row.size() - 1));
        }
        finalMap.remove("NAME");
        return finalMap;
    }

    /**
     * This method gives the program the ability to connect with a given URL
     * @param requestURL is the given URL
     * @return the connection
     * @throws DatasourceException
     * @throws IOException
     */
    public static HttpURLConnection connect(URL requestURL) throws DatasourceException, IOException {
        URLConnection urlConnection = requestURL.openConnection();
        if (!(urlConnection instanceof HttpURLConnection clientConnection))
            throw new DatasourceException("unexpected: result of connection wasn't HTTP");
        clientConnection.connect(); // GET
        if (clientConnection.getResponseCode() != 200)
            throw new DatasourceException("unexpected: API connection not success status " + clientConnection.getResponseMessage());
        return clientConnection;
    }

    /**
     * This method creates the hashmap for county name to county code
     * @param state is the state name
     * @return the hashmap of the county names to county codes for the specified state
     * @throws DatasourceException
     * @throws IOException
     */
    private HashMap<String, String> generateCountyCodes(String state) throws DatasourceException, IOException {
        Moshi moshi = new Moshi.Builder().build();
        Type listObject = Types.newParameterizedType(List.class, List.class, String.class);
        JsonAdapter<List<List<String>>> adapter = moshi.adapter(listObject);
        HashMap<String, String> countyMap;
        URL requestURL = new URL("https", "api.census.gov", "/data/2010/dec/sf1?get=NAME&for=county:*&in=state:" + this.getStateCode(state));
        HttpURLConnection clientConnection = connect(requestURL);
        List<List<String>> body = adapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
        clientConnection.disconnect();
        countyMap = listToMap(body);

        this.countyCache.put(state, countyMap);
        return countyMap;
    }

    /**
     * This method gets the broadband percentage without a given cache
     * @param county is the county name
     * @param state is the state name
     * @return the broadband percentage with the date+time in a string array
     * @throws DatasourceException
     * @throws IOException
     */
    public String[] getBroadband(String county, String state) throws DatasourceException, IOException {
        String stateCode = this.getStateCode(state);
        String countyCode = this.getCountyCode(county, state);
        return this.getCountyAPIResponse(countyCode, stateCode);

    }

    /**
     * This method gets the broadband percentage for a given county given a cache
     * @param cache is the given cache
     * @param county is the specified county name
     * @param state is the state name
     * @return a string array with the broadband percentage and date+time
     * @throws DatasourceException
     * @throws IOException
     */
    public String[] getBroadband(Cache<String, String[]> cache, String county, String state) throws DatasourceException, IOException {
        String[] data;
        String stateCode = this.getStateCode(state);
        String countyCode = this.getCountyCode(county, state);

        if (cache.asMap().containsKey(county + " County, " + state)) {
            String broadband = cache.asMap().get(county + " County, " + state)[0];
            String dateAndTime = cache.asMap().get(county + " County, " + state)[1];
            data = new String[]{broadband, dateAndTime};
        } else {
            data = this.getCountyAPIResponse(countyCode, stateCode);
            cache.put(county + " County, " + state, data);
        }
        return data;
    }

    /**
     * This method gets the broadband percentage given a state code and county code
     * @param countyCode is the county code
     * @param stateCode is the state code
     * @return a string array with broadband percentage and date+time retrieved
     * @throws IOException
     * @throws DatasourceException
     */
    public String[] getCountyAPIResponse(String countyCode, String stateCode) throws IOException, DatasourceException {
        Moshi moshi = new Moshi.Builder().build();
        Type listObject = Types.newParameterizedType(List.class, List.class, String.class);
        JsonAdapter<List<List<String>>> listAdapter = moshi.adapter(listObject);

        URL requestURL = new URL("https", "api.census.gov",
                "/data/2021/acs/acs1/subject/variables?get=NAME,S2802_C03_022E&for=county:" + countyCode + "&in=state:" + stateCode);
        HttpURLConnection clientConnection = connect(requestURL);
        List<List<String>> body = listAdapter.fromJson(new Buffer().readFrom(clientConnection.getInputStream()));
        clientConnection.disconnect();
        String broadband = body.get(1).get(1);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String dateAndTime = dtf.format(now);
        return new String[] {broadband, dateAndTime};
    }
}




package handler;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import spark.Request;
import spark.Response;
import spark.Route;
import csv.CSVSearch;
import exception.SearchException;
import server.Server;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class handles with searching the loaded final
 */
public class SearchHandler implements Route {

    public SearchHandler() {}

    /**
     * This method
     * @param request is the request
     * @param response is the response
     * @return is the 2D Json arrray with the search result
     */
    public Object handle(Request request, Response response) {
        String target = request.queryParams("target");
        String colIdentifier = request.queryParams("identifier");

        Moshi moshi = new Moshi.Builder().build();
        Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
        Map<String, Object> responseMap = new HashMap<>();

        if (!Server.getIsLoaded()) {
            responseMap.put("result", "error_file_not_loaded");
            return adapter.toJson(responseMap);
        }

        if (target != null) {
            CSVSearch searcher;
            if (Server.getHasHeader()) {
                searcher = new CSVSearch(target, Server.getLoadedCSV(), Boolean.TRUE, Server.getHeader());
            }
            else {
                searcher = new CSVSearch(target, Server.getLoadedCSV(), Boolean.FALSE);
            }
            ArrayList<List<String>> searchedRows;
            if (colIdentifier != null) {
                responseMap.put("column identifier", colIdentifier);
                try {
                    searchedRows = searcher.search(colIdentifier);
                }
                catch (SearchException e) {
                    if (Server.getHasHeader()) {
                        responseMap.put("available columns", Server.getHeader());
                    }
                    responseMap.put("result", "error_bad_request");
                    responseMap.put("error", e.getMessage());
                    return adapter.toJson(responseMap);
                }
            } else {
                try {
                searchedRows = searcher.search();
                }
                catch (SearchException e) {
                    responseMap.put("result", "error_bad_request");
                    responseMap.put("error", e.getMessage());
                    return adapter.toJson(responseMap);
                }
            }
            responseMap.put("result", "success");
            responseMap.put("target", target);
            responseMap.put("data", searchedRows);
            return adapter.toJson(responseMap);
        }
        else {
            responseMap.put("result", "error_bad_request");
            return adapter.toJson(responseMap);
        }
    }

}

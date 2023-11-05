package handler;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import spark.Request;
import spark.Response;
import spark.Route;
import csv.CSVParser;
import exception.ParserException;
import csv.RowCreator;
import server.Server;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class loads the filename that the user gives
 */
public class LoadHandler implements Route {

    public LoadHandler() {}

    /**
     * This method gets the filename and loads and parses the file
     * @param request is the request
     * @param response is the response
     * @return is the 2D Json array saying if the load was successful
     */
    public Object handle(Request request, Response response) {
        Moshi moshi = new Moshi.Builder().build();
        Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
        Map<String, Object> responseMap = new HashMap<>();
        try {
            String filename = request.queryParams("filename");
            String header = request.queryParams("header");
            Boolean hasHeader;
            if (filename == null || header == null) {
                responseMap.put("file name", filename);
                responseMap.put("has header?", header);
                responseMap.put("result", "error_bad_request");
                return adapter.toJson(responseMap);
            }
            if (header.equalsIgnoreCase("true")) {
                hasHeader = Boolean.TRUE;
            }
            else if (header.equalsIgnoreCase("false")) {
                hasHeader = Boolean.FALSE;
            }
            else {
                responseMap.put("file name", filename);
                responseMap.put("has header?", header);
                responseMap.put("result", "error_bad_request");
                return adapter.toJson(responseMap);
            }

            try {
                CSVParser<List<String>> parser = new CSVParser<>(new FileReader(filename), hasHeader, new RowCreator());
                ArrayList<List<String>> parsedCSV = parser.parseCSVFile();
                responseMap.put("result", "success");
                responseMap.put("has header?", header);
                responseMap.put("filepath", filename);
                Server.setLoadedCSV(parsedCSV);
                try {
                    Server.setCSVHeader(parser.getHeader());
                    Server.setHasHeader(Boolean.TRUE);
                } catch (ParserException e) {
                    Server.setHasHeader(Boolean.FALSE);
                }

                Server.setIsLoaded(Boolean.TRUE);
                return adapter.toJson(responseMap);
            }
            catch (ParserException e) {
                responseMap.put("result", "error_datasource");
                responseMap.put("error", e.getMessage());
                return adapter.toJson(responseMap);
            }
        }
        catch (FileNotFoundException e) {
            responseMap.put("result", "error_datasource");
            return adapter.toJson(responseMap);
        }
    }

    public Map<String, Object> handleJSON(String filename) throws IOException {
        Moshi moshi = new Moshi.Builder().build();
        Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
        Map<String, Object> responseMap = adapter.fromJson(filename);
        return responseMap;
    }
}

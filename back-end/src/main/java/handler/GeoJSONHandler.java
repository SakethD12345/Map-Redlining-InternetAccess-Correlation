package handler;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.lang.reflect.Type;

import java.util.HashMap;
import java.util.Map;

import com.google.common.cache.Cache;
import datasource.CensusDatasource;
import exception.DatasourceException;

/**
 * This class deals with getting the broadband percentage
 */
public class GeoJSONHandler implements Route {

    public GeoJSONHandler() {
    }

    public Object handle(Request request, Response response) {
        Moshi moshi = new Moshi.Builder().build();
        Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);

        JsonAdapter<Map<String,Object>> jsonAdapter = moshi.adapter(mapStringObject);
        Map<String, Object> responseMap = new HashMap<>();
        return responseMap;
    }
}

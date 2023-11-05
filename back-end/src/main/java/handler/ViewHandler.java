package handler;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import spark.Request;
import spark.Response;
import spark.Route;
import server.Server;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ViewHandler implements Route {
    public ViewHandler() {}
    public Object handle(Request request, Response response) {
        Moshi moshi = new Moshi.Builder().build();
        Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
        JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
        Map<String, Object> responseMap = new HashMap<>();

        if (!Server.getIsLoaded()) {
            responseMap.put("result", "error_file_not_loaded");
            return adapter.toJson(responseMap);
        }

        responseMap.put("result", "success");
        responseMap.put("data", Server.getLoadedCSV());
        return adapter.toJson(responseMap);

    }
}

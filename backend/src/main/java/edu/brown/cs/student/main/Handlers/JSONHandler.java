package edu.brown.cs.student.main.Handlers;

import com.google.common.cache.Cache;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.brown.cs.student.main.Server.GeoJsonCollection;
import edu.brown.cs.student.main.Server.GeoJsonCollection.Feature;
import edu.brown.cs.student.main.Server.GeoJsonCollection.Geometry;
import edu.brown.cs.student.main.Server.GeoJsonCollection.Properties;
import edu.brown.cs.student.main.Server.JsonParsing;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import okio.Buffer;
import spark.Request;
import spark.Response;
import spark.Route;

public class JSONHandler implements Route {

  private CacheProxy cache;
  public JSONHandler(CacheProxy cache){
    this.cache = cache;
  }
  @Override
  public Object handle(Request request, Response response) throws Exception {
    try {
      String minLatitude = request.queryParams("minLat");
      String maxLatitude = request.queryParams("maxLat");
      String minLongitude = request.queryParams("minLong");
      String maxLongitude = request.queryParams("maxLong");

      Moshi moshi = new Moshi.Builder().build();
      Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
      JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
      JsonReader reader = JsonReader.of(new Buffer().writeUtf8(Files.readString(Path.of(
          "src/main/java/edu/brown/cs/student/main/geodata/fullDownload.json"))));
      GeoJsonCollection geoFeature = JsonParsing.fromJsonGeneral(reader, GeoJsonCollection.class);

      if (minLatitude.isEmpty() || maxLatitude.isEmpty() || minLongitude.isEmpty() || maxLongitude.isEmpty()) {
        Map<String,Object> responseMap = new HashMap<>();
        responseMap.put("type", "success");
        responseMap.put("data", JsonParsing.toJsonGeneral(geoFeature));
        return adapter.toJson(responseMap);
      }
      try {
        Double minLat = Double.parseDouble(minLatitude);
        Double maxLat = Double.parseDouble(maxLatitude);
        Double minLong = Double.parseDouble(minLongitude);
        Double maxLong = Double.parseDouble(maxLongitude);

        if (minLat < -90.0 || minLat > 90.0 || maxLat < -90.0 || maxLat > 90.0 ||
            minLong < -180.0 || minLong > 180.0 || maxLong < -180.0 || maxLong > 180.0) {
          Map<String,Object> responseMap = new HashMap<>();
          responseMap.put("result", "error_bad_request");
          responseMap.put("message", "latitude must be between -90 and 90 & longitude must be between -180 and 180");
          return adapter.toJson(responseMap);
        }

        geoFeature.features = filterFeatureByBoundingBox(geoFeature, minLat, maxLat, minLong, maxLong);
        Map<String,Object> responseMap = new HashMap<>();
        responseMap.put("type", "success");
        responseMap.put("data", JsonParsing.toJsonGeneral(geoFeature));
        return adapter.toJson(responseMap);
      } catch (NumberFormatException e) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("result", "error_bad_request");
        responseMap.put("message", "min and max latitute and longitude must be valid double values");
        return adapter.toJson(responseMap);
      }

    } catch(Exception e) {
      Moshi moshi = new Moshi.Builder().build();
      Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
      JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
      Map<String, Object> responseMap = new HashMap<>();
      responseMap.put("result", "failure");
      responseMap.put("error_type", "incorrect query format");
      responseMap.put("error_description", "Search query must include: 'minLat', 'maxLat', 'minLong', and 'maxLong'");
      return adapter.toJson(responseMap);
    }

  }

  public List<Feature> filterFeatureByBoundingBox(GeoJsonCollection geoJsonCollection,
      Double minLat, Double maxLat, Double minLong, Double maxLong){
    List<Double> point = new ArrayList<>();
    point.add(minLat);
    point.add(maxLat);
    point.add(minLong);
    point.add(maxLong);
    if(this.cache.isInCache(point)) {
      return (List<Feature>) this.cache.getValueFromCache(point);
    }
    List<Feature> filteredFeatures = null;
    try {
      filteredFeatures = new ArrayList<>(geoJsonCollection.features);
      //System.out.println(filteredFeatures);
      Iterator<Feature> iterator = filteredFeatures.iterator();
      while (iterator.hasNext()) {
        Feature feature = iterator.next();
        if(feature.geometry == null || feature.geometry.coordinates == null) {
          iterator.remove();
          continue;
        }

        Geometry geometries = feature.geometry;

        List<List<List<List<Double>>>> coordinates = geometries.coordinates;

        if (coordinates.isEmpty() || coordinates.get(0).isEmpty()) {
          iterator.remove();
          continue;
        }
        if(!isInBox(coordinates, minLat, maxLat, minLong, maxLong)) {
          iterator.remove();
        }
      }
    } catch (Exception e) {
      System.out.println(e);
    }
//    System.out.println(filteredFeatures);
    this.cache.addValueToCache(point, filteredFeatures);
    return filteredFeatures;
  }

  public static boolean isInBox(List<List<List<List<Double>>>> coordinates, Double minLat, Double maxLat, Double minLong, Double maxLong) {
    for(List<Double> coordinate: coordinates.get(0).get(0)) {
      Double latitude = coordinate.get(1);
      Double longitude = coordinate.get(0);
      if(latitude < minLat || latitude > maxLat || longitude < minLong || longitude > maxLong) {
        return false;
      }
    }
    return true;
  }
}
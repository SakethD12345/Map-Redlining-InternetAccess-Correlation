package edu.brown.cs.student.main.Handlers;

import com.google.common.cache.Cache;
import com.squareup.moshi.JsonReader;
import edu.brown.cs.student.main.Server.GeoJsonCollection;
import edu.brown.cs.student.main.Server.GeoJsonCollection.Feature;
import edu.brown.cs.student.main.Server.GeoJsonCollection.Geometry;
import edu.brown.cs.student.main.Server.GeoJsonCollection.Properties;
import edu.brown.cs.student.main.Server.JsonParsing;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
      JsonReader reader = JsonReader.of(new Buffer().writeUtf8(Files.readString(Path.of(
          "src/main/java/edu/brown/cs/student/main/geodata/fullDownload.json"))));
      GeoJsonCollection geoFeature = JsonParsing.fromJsonGeneral(reader, GeoJsonCollection.class);
      if (minLatitude.isEmpty() || maxLatitude.isEmpty() || minLongitude.isEmpty() || maxLongitude.isEmpty()) {
        return JsonParsing.toJsonGeneral(geoFeature);
      }
      Double minLat = Double.parseDouble(minLatitude);
      Double maxLat = Double.parseDouble(maxLatitude);
      Double minLong = Double.parseDouble(minLongitude);
      Double maxLong = Double.parseDouble(maxLongitude);
      geoFeature.features = filterFeatureByBoundingBox(geoFeature, minLat, maxLat, minLong, maxLong);
      return JsonParsing.toJsonGeneral(geoFeature);
    } catch(Exception e) {
      return e.getMessage();
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
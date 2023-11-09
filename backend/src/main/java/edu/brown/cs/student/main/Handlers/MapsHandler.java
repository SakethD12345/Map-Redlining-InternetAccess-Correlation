package edu.brown.cs.student.main.Handlers;

import com.squareup.moshi.JsonReader;
import edu.brown.cs.student.main.Server.GeoJsonCollection;
import edu.brown.cs.student.main.Server.GeoJsonCollection.Feature;
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

public class MapsHandler implements Route {
  public MapsHandler(){
  }
  @Override
  public Object handle(Request request, Response response) throws Exception {
    try {
      String area = request.queryParams("area");
      JsonReader reader = JsonReader.of(new Buffer().writeUtf8(Files.readString(Path.of(
          "backend/src/main/java/edu/brown/cs/student/main/geodata/fullDownload.json"))));
      GeoJsonCollection geoFeature = JsonParsing.fromJsonGeneral(reader, GeoJsonCollection.class);

      if (area.isEmpty()) {
        return JsonParsing.toJsonGeneral(geoFeature);
      }
      geoFeature.features = filterFeatureByArea(geoFeature, area);
      return JsonParsing.toJsonGeneral(geoFeature);
    } catch(Exception e) {
      return e;
    }

  }

  public static List<Feature> filterFeatureByArea(GeoJsonCollection geoJsonCollection, String area){
    List<Feature> filteredFeatures = new ArrayList<>(geoJsonCollection.features);
    //System.out.println(filteredFeatures);
    Iterator<Feature> iterator = filteredFeatures.iterator();
    while (iterator.hasNext()) {
      Feature feature = iterator.next();
      Properties properties = feature.properties;
      //System.out.println(properties);
      Map<String, String> description = properties.area_description_data;
      //System.out.println(description);
      if (description==null){
        iterator.remove();
        continue;
      }

      String allData = new String();

      for (String line: description.values())  {
        allData += line;
      }
      System.out.println(allData);
      if (!allData.contains(area))
      {iterator.remove();
        continue;
      }
//      feature.properties.name = "Highlight";
//      feature.properties.holc_grade = "H";
    }
    //System.out.println(filteredFeatures);
    return filteredFeatures;
  }
}
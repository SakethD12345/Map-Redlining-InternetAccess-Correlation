package edu.brown.cs.student.main.Server;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.JsonReader;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class JsonParsing {
  public JsonParsing(){
  }
  /**
   * This is a general JSON serializer that could theoretically be used to change any given type to
   * JSON String. (This is for other developers' use)
   *
   * @param object generic object to serialize
   * @return JSON String for given object(data)
   * @throws IllegalAccessException thrown if the serialization goes wrong
   */
  public static<T> String toJsonGeneral(T object) throws IllegalAccessException {
    Field[] fields = object.getClass().getDeclaredFields();

    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("return Type", "success");
    for (Field field : fields) {
      responseMap.put(field.getName(), field.get(object));
    }
    return adapter.toJson(responseMap);
  }

  public static <T> T fromJsonGeneral(JsonReader source, Class<T> targetType) throws IOException {
    Moshi moshi = new Moshi.Builder().build();
    JsonAdapter<T> adapter = moshi.adapter(targetType);
    source.setLenient(true);

    return adapter.fromJson(source);
  }
}

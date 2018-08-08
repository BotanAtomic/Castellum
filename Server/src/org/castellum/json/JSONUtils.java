package org.castellum.json;

import org.json.JSONObject;

import java.util.Map;

public class JSONUtils {

    private final JSONObject object;

    public JSONUtils(JSONObject object) {
        this.object = object;
    }

    public String getString(String data) {
        return object.getString(data);
    }


    public static void implementInMap(Map<String, Object> map, JSONObject object) {
        object.keySet().forEach(key -> {
            Object value = object.get(key);

            if (value instanceof JSONObject) {
                implementInMap(map, (JSONObject) value);
            } else {
                map.put(key, value);
            }

        });
    }

}

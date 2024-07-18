package easy.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * JSON数据处理(Gson)
 *
 * @author mabin
 * @project EasyTool
 * @package easy.util
 * @date 2024/03/16 11:12
 */
public class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create();

    private JsonUtil() {
    }

    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        return GSON.toJson(object);
    }

    public static String toPrettyJson(Object object) {
        if (object == null) {
            return null;
        }
        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .setPrettyPrinting()
                .create();
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        return GSON.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type type) {
        if (json == null) {
            return null;
        }
        return GSON.fromJson(json, type);
    }

    public static <T> Map<String, T> fromMap(String json) {
        if (json == null) {
            return null;
        }
        return GSON.fromJson(json, new TypeToken<Map<String, T>>() {
        }.getType());
    }

    public static <T> List<Map<String, T>> fromListMap(String json) {
        return GSON.fromJson(json, new TypeToken<List<Map<String, T>>>() {
        }.getType());
    }

    public static JsonObject fromObject(String json) {
        if (json == null) {
            return null;
        }
        JsonElement element = JsonParser.parseString(json);
        if (element.isJsonObject()) {
            return element.getAsJsonObject();
        }
        return null;
    }

    public static JsonArray fromArray(String json) {
        if (json == null) {
            return null;
        }
        JsonElement element = JsonParser.parseString(json);
        if (element.isJsonArray()) {
            return element.getAsJsonArray();
        }
        return null;
    }

    public static Boolean isJson(String json) {
        try {
            fromJson(json, JsonElement.class);
            return Boolean.TRUE;
        } catch (Exception e) {
            return Boolean.FALSE;
        }
    }

    public static String findJson(String json, String fieldName) {
        if (Boolean.TRUE.equals(isJson(json))) {
            JsonElement jsonElement = findJson(fromJson(json, JsonElement.class), fieldName);
            return jsonElement.isJsonNull() ? null : jsonElement.getAsString();
        }
        return null;
    }

    private static JsonElement findJson(JsonElement jsonElement, String fieldName) {
        if (jsonElement == null) {
            return JsonNull.INSTANCE;
        }
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                if (entry.getKey().equals(fieldName)) {
                    return entry.getValue();
                } else {
                    JsonElement foundValue = findJson(entry.getValue(), fieldName);
                    if (!foundValue.isJsonNull()) {
                        return foundValue;
                    }
                }
            }
        } else if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                JsonElement foundValue = findJson(element, fieldName);
                if (!foundValue.isJsonNull()) {
                    return foundValue;
                }
            }
        }
        return JsonNull.INSTANCE;
    }

}

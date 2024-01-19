package easy.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JSON数据处理(Gson)
 *
 * @author mabin
 * @project EasyChar
 * @date 2023/09/05 11:47
 **/

public class JsonUtil {

    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .setPrettyPrinting()
            .create();

    private JsonUtil() {
    }

    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        return GSON.toJson(object);
    }

    public static <T> T fromJson(@NotNull String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        return GSON.fromJson(json, clazz);
    }

    public static <T> Map<String, T> fromMap(@NotNull String json) {
        if (json == null) {
            return null;
        }
        return GSON.fromJson(json, new TypeToken<Map<String, T>>() {
        }.getType());
    }

    public static <T> List<Map<String, T>> fromListMap(@NotNull String json) {
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


}

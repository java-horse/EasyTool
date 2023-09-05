package easy.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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

    private static final Gson GSON = new Gson();

    private JsonUtil() {
    }

    public static String toJson(Object object) {
        if (object == null) {
            return null;
        }
        return GSON.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        return GSON.fromJson(json, clazz);
    }

    public static <T> List<T> fromList(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }
        JsonArray array = new JsonParser().parse(json).getAsJsonArray();
        List<T> list = new ArrayList();
        for (final JsonElement elem : array) {
            list.add(GSON.fromJson(elem, clazz));
        }
        return list;
    }

    public static <T> List<Map<String, T>> fromListMap(String json) {
        if (json == null) {
            return null;
        }
        return GSON.fromJson(json, new TypeToken<List<Map<String, T>>>() {
        }.getType());
    }

    public static <T> Map<String, T> fromMap(String json) {
        if (json == null) {
            return null;
        }
        return GSON.fromJson(json, new TypeToken<Map<String, T>>() {
        }.getType());
    }

}

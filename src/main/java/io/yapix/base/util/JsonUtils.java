package io.yapix.base.util;

import com.google.gson.Gson;
import java.lang.reflect.Type;

public class JsonUtils {

    private static final Gson gson = new Gson();

    public static String toJson(Object data) {
        return gson.toJson(data);
    }

    public static <T> T fromJson(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

}

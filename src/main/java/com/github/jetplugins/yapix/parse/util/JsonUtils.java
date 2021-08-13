package com.github.jetplugins.yapix.parse.util;

import com.google.gson.Gson;

public class JsonUtils {

    private static final Gson gson = new Gson();

    public static String toJson(Object data) {
        return gson.toJson(data);
    }

}

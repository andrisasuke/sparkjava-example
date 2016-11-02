package com.hydra.spark.sample.util;

import com.google.gson.Gson;
import spark.ResponseTransformer;

public class JsonUtil {

    public static final Gson GSON = new Gson();

    public static String toJson(Object object) {
        return GSON.toJson(object);
    }

    public static ResponseTransformer json() {
        return JsonUtil::toJson;
    }

}

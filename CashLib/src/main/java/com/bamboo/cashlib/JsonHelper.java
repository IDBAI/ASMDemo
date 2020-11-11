package com.bamboo.cashlib;


import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by liushuai on 17/04/19.
 * Json JsonHelper/Deserializer.
 */

public class JsonHelper {
    private static final String TAG = "JsonHelper";

    private static final Gson gson = new Gson();

    JsonHelper() {
    }

    /**
     * Serialize an object to Json.
     *
     * @param object to toJson.
     */
    public static String toJson(Object object) {
        try {
            return gson.toJson(object);
        } catch (Exception e) {
            return "";
        }
    }

    public static String toJson(Object object, Type type) {

        try {
            return gson.toJson(object, type);
        } catch (Exception e) {
            return "";
        }

    }

    /**
     * Deserialize a json representation of an object.
     *
     * @param string A json string to fromJson.
     */
    public static <T> T fromJson(String string, Class<T> clazz) {
        try {
            return gson.fromJson(string, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T fromJson(String string, Type type) {
        try {
            return gson.fromJson(string, type);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 较大的json数据，应该使用reader的方式去解析，减少构造string的内存分配
     *
     * @param reader
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T fromJson(Reader reader, Class<T> clazz) {
        try {
            return gson.fromJson(reader, clazz);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T fromJson(Map map, Class<T> clazz) {
        try {
            JsonElement jsonElement = gson.toJsonTree(map);
            return gson.fromJson(jsonElement, clazz);
        } catch (Exception e) {
            return null;
        }
    }


}

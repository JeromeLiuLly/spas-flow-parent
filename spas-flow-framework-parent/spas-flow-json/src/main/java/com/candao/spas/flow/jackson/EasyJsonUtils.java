package com.candao.spas.flow.jackson;

import com.jn.easyjson.core.JSON;
import com.jn.easyjson.core.JsonTreeNode;
import com.jn.easyjson.jackson.JacksonJSONBuilder;
import com.jn.langx.util.reflect.type.Types;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;


public final class EasyJsonUtils {
    private volatile static EasyJsonUtils instance = null;
    final JSON json;

    private EasyJsonUtils() {
        this.json = new JacksonJSONBuilder().build();
    }

    // java中使用双重检查锁定机制
    public static EasyJsonUtils getInstance() {
        if (instance == null) {
            synchronized (EasyJsonUtils.class) {
                if (instance == null) {
                    instance = new EasyJsonUtils();
                }
            }
        }
        return instance;
    }

    public static String toJsonString(Object obj) {
        if (obj == null)
            return "";
        return getInstance().json.toJson(obj);
    }

    public static byte[] toJsonBytes(Object obj) {
        if (obj == null)
            return null;
        return getInstance().json.toJson(obj).getBytes();
    }

    public static String toJsonString(Object obj, Type typeOfT) {
        if (obj == null)
            return "";
        return getInstance().json.toJson(obj, typeOfT);
    }

    public static JsonTreeNode toJavaObject(String json) {
        if (json == null)
            return null;
        return getInstance().json.fromJson(json);
    }

    public static <T> T toJavaObject(String json, Type typeOfT) {
        if (json == null)
            return null;
        return getInstance().json.fromJson(json, typeOfT);
    }

    public static <T> T toJavaObject(String json, Class<T> classOfT) {
        if (json == null)
            return null;
        return getInstance().json.fromJson(json, classOfT);
    }

    public static <T> T toJavaObject(Reader reader, Class<T> classOfT) {
        if (reader == null)
            return null;
        return getInstance().json.fromJson(reader, classOfT);
    }

    public static <T> T toJavaObject(Object obj, Type typeOfT) {
        if (obj == null)
            return null;
        if (obj instanceof String) {
            return toJavaObject((String) obj, typeOfT);
        }
        return getInstance().json.fromJson(toJsonString(obj), typeOfT);
    }

    public static <T> List<T> toJavaList(String json, Class<T> classOfT) {
        if (json == null)
            return null;
        Type type = Types.getListParameterizedType(classOfT);
        return getInstance().json.fromJson(json, type);
    }

    public static <T> List<T> toJavaList(String json, Type typeOfT) {
        if (json == null)
            return null;
        Type type = Types.getListParameterizedType(typeOfT);
        return getInstance().json.fromJson(json, type);
    }

    public static <T> T toJavaCollection(String json, Class<? extends Collection<T>> classofCollection, Class<T> classOfT) {
        if (json == null)
            return null;
        Type type = Types.getParameterizedType(classofCollection, classOfT);
        return getInstance().json.fromJson(json, type);
    }
}
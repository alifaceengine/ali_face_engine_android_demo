package com.alibaba.cloud.alifaceenginedemo;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.cloud.faceengine.Mode;
import com.alibaba.cloud.faceengine.ModelType;

import java.util.Map;


public class SPUtils {

    public static final String FILE_NAME = "my_sp";
    public static final String KEY_AUTH_KEY = "auth_key";
    public static final String KEY_RUN_MODE = "run_mode";

    public static final String KEY_USE_CLOUD = "user_cloud";
    public static final Boolean DEFAULT_VALUE_USE_CLOUD = true;

    public static final String KEY_CLOUD_IP = "cloud_ip";
    public static final String KEY_CLOUD_PORT = "cloud_port";
    public static final String KEY_CLOUD_USERNAME = "cloud_username";
    public static final String KEY_CLOUD_USERPSW = "cloud_userpsw";

    public static void put(Context context, String key, Object obj) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (obj instanceof Boolean) {
            editor.putBoolean(key, (Boolean) obj);
        } else if (obj instanceof Float) {
            editor.putFloat(key, (Float) obj);
        } else if (obj instanceof Integer) {
            editor.putInt(key, (Integer) obj);
        } else if (obj instanceof Long) {
            editor.putLong(key, (Long) obj);
        } else {
            editor.putString(key, (String) obj);
        }
        editor.commit();
    }


    public static Object get(Context context, String key, Object defaultObj) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, context.MODE_PRIVATE);
        if (defaultObj instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObj);
        } else if (defaultObj instanceof Float) {
            return sp.getFloat(key, (Float) defaultObj);
        } else if (defaultObj instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObj);
        } else if (defaultObj instanceof Long) {
            return sp.getLong(key, (Long) defaultObj);
        } else if (defaultObj instanceof String) {
            return sp.getString(key, (String) defaultObj);
        }
        return null;
    }

    public static boolean hasAuthKey(Context context) {
        String key = (String) get(context, KEY_AUTH_KEY, "");
        if (key == null) {
            return false;
        } else if (key.trim().length() == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static String getAuthKey(Context context) {
        return (String) get(context, KEY_AUTH_KEY, "");
    }

    public static void setAuthKey(Context context, String key) {
        put(context, KEY_AUTH_KEY, key);
    }

    public static int getRunMode(Context context) {
        if (getUseCloud(context)) {
            return (int) get(context, KEY_RUN_MODE, Mode.CLOUD);
        } else {
            return (int) get(context, KEY_RUN_MODE, Mode.TERMINAL);
        }
    }

    public static boolean getUseCloud(Context context) {
        return (boolean) get(context, KEY_USE_CLOUD, DEFAULT_VALUE_USE_CLOUD);
    }

    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }


    public static Map<String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, context.MODE_PRIVATE);
        Map<String, ?> map = sp.getAll();
        return map;
    }

    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, context.MODE_PRIVATE);
        return sp.contains(key);
    }
}

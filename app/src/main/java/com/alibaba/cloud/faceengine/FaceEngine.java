package com.alibaba.cloud.faceengine;

/**
 * Created by junyuan.hjy on 2018/8/24.
 */

public class FaceEngine {
    public static String getVersion() {
        return AliFaceEngineJNI.getVersion();
    }

    public static String getVendorId() {
        return AliFaceEngineJNI.getVendorId();
    }

    public static int authorize(String key) {
        return AliFaceEngineJNI.authorize(key);
    }

    public static void setCloudAddr(String ip, int port) {
        AliFaceEngineJNI.setCloudAddr(ip, port);
    }

    public static void setCloudLoginAccount(String cId, String cSecret) {
        AliFaceEngineJNI.setCloudLoginAccount(cId, cSecret);
    }

    public static void enableDebug(boolean enable) {
        if (enable) {
            AliFaceEngineJNI.enableDebug(1);
        } else {
            AliFaceEngineJNI.enableDebug(0);
        }
    }

    public static int setPersistencePath(String path) {
        return AliFaceEngineJNI.setPersistencePath(path);
    }

    public static boolean supportGPU() {
        return AliFaceEngineJNI.supportGPU();
    }

    public static void setConfigString(String key, String value) {
        AliFaceEngineJNI.setConfigString(key, value);
    }

    public static void setConfigInt(String key, int value) {
        AliFaceEngineJNI.setConfigInt(key, value);
    }

    public static void setConfigFloat(String key, float value) {
        AliFaceEngineJNI.setConfigFloat(key, value);
    }

    public static String getConfigString(String key, String defaultValue) {
        return AliFaceEngineJNI.getConfigString(key, defaultValue);
    }

    public static int getConfigInt(String key, int defaultValue) {
        return AliFaceEngineJNI.getConfigInt(key, defaultValue);
    }

    public static float getConfigFloat(String key, float defaultValue) {
        return AliFaceEngineJNI.getConfigFloat(key, defaultValue);
    }

}

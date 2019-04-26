package com.alibaba.cloud.faceengine;

/**
 * Created by junyuan.hjy on 2018/8/24.
 */

public class FaceEngine {
    public static String getVersion() {
        return AliFaceEngineJNI.getVersion();
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

    public static void setPersistencePath(String path) {
        AliFaceEngineJNI.setPersistencePath(path);
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

}

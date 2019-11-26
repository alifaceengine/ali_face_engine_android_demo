package com.alibaba.cloud.faceengine;

public class AliFaceEngineJNI {

    public static native String getVersion();

    public static native int authorize(String key);

    public static native int unInitialize();

    public static native boolean isAuthorized();

    public static native boolean supportCloud();

    public static native String getVendorId();

    public static native void setCloudAddr(String ip, int port);

    public static native void setCloudLoginAccount(String cId, String cSecret);

    public static native void enableDebug(int enable);

    public static native int setPersistencePath(String path);

    public static native boolean supportGPU();

    public static native void setConfigString(String key, String value);

    public static native void setConfigInt(String key, int value);

    public static native void setConfigFloat(String key, float value);

    public static native String getConfigString(String key, String defaultValue);

    public static native int getConfigInt(String key, int defaultValue);

    public static native float getConfigFloat(String key, float defaultValue);

    static {
        System.loadLibrary("AliFaceEngineJNI");
    }
}

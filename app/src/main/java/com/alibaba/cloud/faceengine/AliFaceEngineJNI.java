package com.alibaba.cloud.faceengine;

/**
 * Created by junyuan.hjy on 2018/8/24.
 */

public class AliFaceEngineJNI {

    public static native String getVersion();

    public static native int authorize(String key);

    public static native void setCloudAddr(String ip, int port);

    public static native void setCloudLoginAccount(String cId, String cSecret);

    public static native void enableDebug(int enable);

    public static native void setPersistencePath(String path);

    public static native void setConfigString(String key, String value);

    public static native void setConfigInt(String key, int value);

    public static native void setConfigFloat(String key, float value);

    static {
        System.loadLibrary("AliFaceEngineJNI");
    }
}

package com.alibaba.cloud.faceengine;

public class FaceAttributeAnalyzeJNI {
    public static native long createInstance(int mode);

    public static native void deleteInstance(long context);

    public static native void setFlag(long context, int flag);

    public static native int getFlag(long context);

    public static native int analyze(long context, Image image, Face face);

    public static native int analyze2(long context, Image image, Face[] faces);

    static {
        System.loadLibrary("AliFaceEngineJNI");
    }
}

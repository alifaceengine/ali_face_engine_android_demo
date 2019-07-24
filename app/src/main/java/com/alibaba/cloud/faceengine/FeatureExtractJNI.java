package com.alibaba.cloud.faceengine;

class FeatureExtractJNI {
    public static native long createInstance(int modelType, int mode);

    public static native void deleteInstance(long context);

    public static native String extractFeature(long context, Image image, Face face);

    static {
        System.loadLibrary("AliFaceEngineJNI");
    }
}

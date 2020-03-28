package com.alibaba.cloud.faceengine;

class FeatureExtractJNI {
    public static native long createInstance(int modelType, int mode);

    public static native void deleteInstance(long context);

    public static native String extractFeature(long context, Image image, Face face);

    public static native String extractFeature2(long context, Image image, float fp0_x, float fp0_y,
                                                float fp1_x, float fp1_y,
                                                float fp2_x, float fp2_y,
                                                float fp3_x, float fp3_y,
                                                float fp4_x, float fp4_y);

    static {
        System.loadLibrary("AliFaceEngineJNI");
    }
}

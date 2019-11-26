package com.alibaba.cloud.faceengine;

class FaceRecognizeNoDBJNI {
    public static native long createInstance(String groupId);

    public static native void deleteInstance(long context);

    public static native int addPerson(long context, String personId);

    public static native int deletePerson(long context, String personId);

    public static native int deleteManyPersons(long context, String[] personIds);

    public static native int deleteAllPersons(long context);

    public static native int getPersonNum(long context);

    public static native int addFeature(long context, String personId, String featureId, String feature);

    public static native int deleteFeature(long context, String personId, String featureId);

    public static native int deleteAllFeatures(long context, String personId);

    public static native int getFeatureNumOfGroup(long context);

    public static native int getFeatureNum(long context, String personId);

    public static native RecognizeResult recognizeFeature(long context, String feature);

    public static native RecognizeResult[] recognizeFeature2(long context, String feature, int topN);

    static {
        System.loadLibrary("AliFaceEngineJNI");
    }
}

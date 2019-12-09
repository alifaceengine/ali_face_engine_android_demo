package com.alibaba.cloud.faceengine;

public class FaceRecognizeNoDB {

    public static FaceRecognizeNoDB createInstance(String groupId) {
        long context = FaceRecognizeNoDBJNI.createInstance(groupId);
        if (context == 0) {
            return null;
        }

        FaceRecognizeNoDB ins = new FaceRecognizeNoDB();
        ins.mContext = context;
        return ins;
    }

    public static void deleteInstance(FaceRecognizeNoDB ins) {
        if (ins != null) {
            FaceRecognizeNoDBJNI.deleteInstance(ins.mContext);
        }
    }

    protected long mContext;

    public int addPerson(String personId) {
        return FaceRecognizeNoDBJNI.addPerson(mContext, personId);
    }

    public int deletePerson(String personId) {
        return FaceRecognizeNoDBJNI.deletePerson(mContext, personId);
    }

    public int deleteManyPersons(String[] personIds) {
        return FaceRecognizeNoDBJNI.deleteManyPersons(mContext, personIds);
    }

    public int deleteAllPersons() {
        return FaceRecognizeNoDBJNI.deleteAllPersons(mContext);
    }

    public int getPersonNum() {
        return FaceRecognizeNoDBJNI.getPersonNum(mContext);
    }

    public int addFeature(String personId, String featureId, String feature) {
        return FaceRecognizeNoDBJNI.addFeature(mContext, personId, featureId, feature);
    }

    public int deleteFeature(String personId, String featureId) {
        return FaceRecognizeNoDBJNI.deleteFeature(mContext, personId, featureId);
    }

    public int deleteAllFeatures(String personId) {
        return FaceRecognizeNoDBJNI.deleteAllFeatures(mContext, personId);
    }

    public int getFeatureNumOfGroup() {
        return FaceRecognizeNoDBJNI.getFeatureNumOfGroup(mContext);
    }

    public int getFeatureNum(String personId) {
        return FaceRecognizeNoDBJNI.getFeatureNum(mContext, personId);
    }

    public RecognizeResult recognizeFeature(String feature) {
        return FaceRecognizeNoDBJNI.recognizeFeature(mContext, feature);
    }

    public RecognizeResult[] recognizeFeature(String feature, int topN) {
        return FaceRecognizeNoDBJNI.recognizeFeature2(mContext, feature, topN);
    }

}

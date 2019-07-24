package com.alibaba.cloud.faceengine;

public class FeatureExtract {

    public static FeatureExtract createInstance(int modelType, int mode) {
        long context = FeatureExtractJNI.createInstance(modelType, mode);
        if (context == 0) {
            return null;
        }

        FeatureExtract ins = new FeatureExtract();
        ins.mContext = context;
        return ins;
    }

    public static void deleteInstance(FeatureExtract ins) {
        if (ins != null) {
            FeatureExtractJNI.deleteInstance(ins.mContext);
        }
    }

    public String extractFeature(Image image, Face face) {
        return FeatureExtractJNI.extractFeature(mContext, image, face);
    }

    protected long mContext;
}

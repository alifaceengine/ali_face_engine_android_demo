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
        if (image == null || image.data == null) {
            return null;
        }
        return FeatureExtractJNI.extractFeature(mContext, image, face);
    }

    public String extractFeature(Image image, float fp0_x, float fp0_y,
                                 float fp1_x, float fp1_y,
                                 float fp2_x, float fp2_y,
                                 float fp3_x, float fp3_y,
                                 float fp4_x, float fp4_y) {
        if (image == null || image.data == null) {
            return null;
        }
        return FeatureExtractJNI.extractFeature2(mContext, image,
                fp0_x, fp0_y, fp1_x, fp1_y, fp2_x, fp2_y, fp3_x, fp3_y, fp4_x, fp4_y);
    }

    protected long mContext;
}

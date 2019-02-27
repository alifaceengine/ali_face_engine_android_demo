package com.alibaba.cloud.faceengine;

public class FaceAttributeAnalyze {

    public static final int QUALITY = 0x1;
    public static final int LIVENESS = 0x2;
    public static final int AGE = 0x4;
    public static final int GENDER = 0x8;
    public static final int EXPRESSION = 0x10;
    public static final int GLASS = 0x20;

    private long mContext;

    public static FaceAttributeAnalyze createInstance(int mode) {
        long context = FaceAttributeAnalyzeJNI.createInstance(mode);
        if (context == 0) {
            return null;
        }

        return new FaceAttributeAnalyze(context);
    }

    public static void deleteInstance(FaceAttributeAnalyze ins) {
        if (ins != null) {
            FaceAttributeAnalyzeJNI.deleteInstance(ins.getContext());
        }
    }

    protected FaceAttributeAnalyze(long context) {
        mContext = context;
    }

    protected long getContext() {
        return mContext;
    }

    public void setFlag(int flag) {
        FaceAttributeAnalyzeJNI.setFlag(mContext, flag);
    }

    public int getFlag() {
        return FaceAttributeAnalyzeJNI.getFlag(mContext);
    }

    public int analyze(Image image, Face face) {
        return FaceAttributeAnalyzeJNI.analyze(mContext, image, face);
    }

    public int analyze(Image image, Face[] faces) {
        return FaceAttributeAnalyzeJNI.analyze2(mContext, image, faces);
    }

    static {
        System.loadLibrary("AliFaceEngineJNI");
    }
}

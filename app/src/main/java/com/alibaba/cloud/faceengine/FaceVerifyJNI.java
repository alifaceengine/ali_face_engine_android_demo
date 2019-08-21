package com.alibaba.cloud.faceengine;

class FaceVerifyJNI {
    public static native long createInstance(int mode);

    public static native void deleteInstance(long context);

    public static native void setVerifyVideoListener(long context, FaceVerify.VerifyVideoListener listener);

    public static native FaceVerify.VerifyVideoListener getVerifyVideoListener(long context);

    public static native int verifyVideo(long context, Image image, Face faces[]);

    public static native int registerFace(long context, Image image, Face face);

    public static native int registerFeature(long context, String feature);

    public static native VerifyResult[] verifyPicture(long context, Image image1, Face image1Face, Image image2, Face image2Faces[]);

    static {
        System.loadLibrary("AliFaceEngineJNI");
    }
}

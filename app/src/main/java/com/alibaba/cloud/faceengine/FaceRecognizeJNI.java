package com.alibaba.cloud.faceengine;

/**
 * Created by junyuan.hjy on 2018/8/29.
 */

class FaceRecognizeJNI {
    public static native long createInstance(int mode);

    public static native void deleteInstance(long context);

    public static native void setRecognizeVideoListener(long context, FaceRecognize.RecognizeVideoListener listener);

    public static native FaceRecognize.RecognizeVideoListener getRecognizeVideoListener();

    public static native int setGroupId(long context, String groupId);

    public static native int reloadDB(long context);

    public static native int recognizeVideo(long context, Image image, Face[] faces);

    public static native RecognizeResult[] recognizePicture(long context, Image image, Face[] faces);

    static {
        System.loadLibrary("AliFaceEngineJNI");
    }
}

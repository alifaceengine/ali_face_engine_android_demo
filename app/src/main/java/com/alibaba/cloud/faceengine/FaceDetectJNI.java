package com.alibaba.cloud.faceengine;

/**
 * Created by junyuan.hjy on 2018/8/29.
 */

public class FaceDetectJNI {
    public static native long createInstance(int mode);

    public static native void deleteInstance(long context);

    public static native int setPictureParameter(long context, DetectParameter parameter);

    public static native DetectParameter getPictureParameter(long context);

    public static native Face[] detectPicture(long context, Image image);

    public static native int setVideoParameter(long context, DetectParameter parameter);

    public static native DetectParameter getVideoParameter(long context);

    public static native Face[] detectVideo(long context, Image image);

    static {
        System.loadLibrary("AliFaceEngineJNI");
    }
}

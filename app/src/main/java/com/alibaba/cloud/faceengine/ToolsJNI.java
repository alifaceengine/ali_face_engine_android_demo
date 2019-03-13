package com.alibaba.cloud.faceengine;

/**
 * Created by junyuan.hjy on 2018/8/30.
 */

public class ToolsJNI {
    public static native float compareFeatures(String feature1, String feature2);

    public static native void drawFaceRect(Image image, Face face, int color);

    static {
        System.loadLibrary("AliFaceEngineJNI");
    }
}

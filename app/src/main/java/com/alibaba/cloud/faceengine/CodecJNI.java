package com.alibaba.cloud.faceengine;

/**
 * Created by junyuan.hjy on 2018/9/3.
 */

public class CodecJNI {
    public static native void nv21Rotate90InPlace(byte[] src, int width, int height);

    public static native void nv21Rotate180InPlace(byte[] src, int width, int height);

    public static native void nv21Rotate270InPlace(byte[] src, int width, int height);

    static {
        System.loadLibrary("AliFaceEngineJNI");
    }
}

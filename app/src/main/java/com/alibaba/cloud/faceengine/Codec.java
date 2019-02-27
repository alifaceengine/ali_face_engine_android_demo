package com.alibaba.cloud.faceengine;

/**
 * Created by junyuan.hjy on 2018/9/3.
 */

public class Codec {
    public static void nv21Rotate90InPlace(byte[] src, int width, int height) {
        CodecJNI.nv21Rotate90InPlace(src, width, height);
    }

    public static void nv21Rotate180InPlace(byte[] src, int width, int height) {
        CodecJNI.nv21Rotate180InPlace(src, width, height);
    }

    public static void nv21Rotate270InPlace(byte[] src, int width, int height) {
        CodecJNI.nv21Rotate270InPlace(src, width, height);
    }
}

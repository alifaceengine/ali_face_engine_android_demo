package com.alibaba.cloud.faceengine;

/**
 * Created by junyuan.hjy on 2018/8/27.
 */

public class ImageFormat {
    public static final int ImageFormat_UNKNOWN = -1;
    public static final int RGB888 = 0;
    public static final int BGR888 = 1;
    public static final int NV21 = 10;
    //public static final int NV12 = 11;
    public static final int YV12 = 12;
    public static final int I420 = 13;
    public static final int JPEG = 20;
    public static final int PNG = 21;
    public static final int BMP = 22;

    private ImageFormat() {
    }
}

package com.alibaba.cloud.faceengine;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

    public static byte[] rgb888ToJpeg(byte[] src, int width, int height, int quality) {
        return CodecJNI.rgb888ToJpeg(src, width, height, quality);
    }

    public static byte[] rgb888ToBmp(byte[] src, int width, int height) {
        return CodecJNI.rgb888ToBmp(src, width, height);
    }
}

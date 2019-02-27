package com.alibaba.cloud.faceengine;

/**
 * Created by junyuan.hjy on 2018/8/30.
 */

public class Tools {
    public static void drawFaceRect(Image image, Face face, int color) {
        ToolsJNI.drawFaceRect(image, face, color);
    }
}

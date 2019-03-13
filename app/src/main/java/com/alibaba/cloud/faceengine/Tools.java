package com.alibaba.cloud.faceengine;

/**
 * Created by junyuan.hjy on 2018/8/30.
 */

public class Tools {
    public static float compareFeatures(String feature1, String feature2) {
        return ToolsJNI.compareFeatures(feature1, feature2);
    }

    public static void drawFaceRect(Image image, Face face, int color) {
        ToolsJNI.drawFaceRect(image, face, color);
    }
}
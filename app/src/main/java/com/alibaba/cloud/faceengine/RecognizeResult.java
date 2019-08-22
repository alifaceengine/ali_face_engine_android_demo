package com.alibaba.cloud.faceengine;

/**
 * Created by junyuan.hjy on 2018/8/29.
 */

public class RecognizeResult {
    public int trackId;
    public String personId;
    public String personName;
    public String personTag;
    public float similarity;
    public String feature;
    public Face face;

    @Override
    public String toString() {
        return "RecognizeResult{" +
                "trackId=" + trackId +
                ", personId='" + personId + '\'' +
                ", personName='" + personName + '\'' +
                ", personTag='" + personTag + '\'' +
                ", similarity=" + similarity +
                '}';
    }
}

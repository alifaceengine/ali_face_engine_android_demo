package com.alibaba.cloud.faceengine;

/**
 * Created by junyuan.hjy on 2018/8/29.
 */

public class RecognizeResult {
    public int trackId;
    public String personId;
    public String personName;
    public float similarity;

    @Override
    public String toString() {
        return "RecognizeResult{" +
                "trackId=" + trackId +
                ", personId='" + personId + '\'' +
                ", personName='" + personName + '\'' +
                ", similarity=" + similarity +
                '}';
    }
}

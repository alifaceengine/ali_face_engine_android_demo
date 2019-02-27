package com.alibaba.cloud.faceengine;

/**
 * Created by junyuan.hjy on 2018/8/31.
 */

public class VerifyResult {
    public int trackId;
    public float similarity;

    @Override
    public String toString() {
        return "VerifyResult{" +
                "trackId=" + trackId +
                ", similarity=" + similarity +
                '}';
    }
}

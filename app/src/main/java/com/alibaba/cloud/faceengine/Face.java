package com.alibaba.cloud.faceengine;

/**
 * Created by junyuan.hjy on 2018/8/24.
 */

public class Face {
    public int trackId;
    public int confidence;
    public Rect rect;
    public Pose pose;
    public FacePoint facePoints[];
    public Attribute attribute;

    @Override
    public String toString() {
        return "trackId:" + trackId + " confidence:" + confidence + " rect:" + rect + " pose:" + pose + " attribute:" + attribute;
    }
}

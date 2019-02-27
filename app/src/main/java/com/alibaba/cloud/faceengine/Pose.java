package com.alibaba.cloud.faceengine;

/**
 * Created by junyuan.hjy on 2018/8/24.
 */

public class Pose {
    public float pitch;
    public float yaw;
    public float roll;

    @Override
    public String toString() {
        return "pitch:" + pitch + " yaw:" + yaw + " roll:" + roll;
    }
}

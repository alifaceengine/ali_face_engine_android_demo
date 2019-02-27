package com.alibaba.cloud.faceengine;

/**
 * Created by junyuan.hjy on 2018/8/24.
 */

public class Attribute {
    public Quality quality;
    public Liveness liveness;
    public int age;
    public int gender;
    public int expression;
    public int glass;

    @Override
    public String toString() {
        return "Attribute{" +
                "quality=" + quality +
                ", liveness=" + liveness +
                ", age=" + age +
                ", gender=" + gender +
                ", expression=" + expression +
                ", glass=" + glass +
                '}';
    }
}

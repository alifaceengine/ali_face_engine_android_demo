package com.alibaba.cloud.faceengine;

/**
 * Created by junyuan.hjy on 2018/8/24.
 */

public class Rect {
    public int left;
    public int top;
    public int right;
    public int bottom;

    @Override
    public String toString() {
        return "left:" + left + " top:" + top + " right:" + right + " bottom:" + bottom;
    }
}
